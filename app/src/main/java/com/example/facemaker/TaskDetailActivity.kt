package com.example.facemaker

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.ActivityTaskDetailBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val REMOVED_TASK_ID = "removedTaskId"

class TaskDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailBinding
    private lateinit var database: DatabaseReference
    private var currentTask: Task? = null

    private lateinit var taskStepAdapter: TaskStepAdapter
    private lateinit var taskDateAdapter: TaskDateAdapter
    private lateinit var taskFileAdapter: TaskFileAdapter

    var mCurrentPhotoPath: String = ""
    var filename: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_detail)
        database = Firebase.database.reference

        val bundle: Bundle? = intent.extras
        val currentTaskId: String = bundle?.getString(TASK_ID) ?: return

        val taskListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentTask = snapshot.getValue<Task>()

                if (currentTask == null) {
                    return
                }

                binding.taskNameText.setText(currentTask!!.name, TextView.BufferType.EDITABLE)
                binding.taskCheckButton.isChecked = (currentTask!!.completionDateTime != null)

                // 체크박스 클릭했을 때 이벤트 추가
                binding.taskCheckButton.setOnClickListener {
                    if (binding.taskCheckButton.isChecked) {
                        currentTask!!.completionDateTime = Calendar.getInstance().time
                    } else {
                        currentTask!!.completionDateTime = null
                    }

                    database.child("tasks/${currentTask!!.id}/completionDateTime")
                        .setValue(currentTask!!.completionDateTime)
                }

                // 변경될 때마다가 아니라 초점을 잃었을 때만 변경해야하지만
                // 임시로 EditText 가 변경될 때 이벤트 추가
                binding.taskNameText.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        currentTask?.name = binding.taskNameText.text.toString()
                    }
                }

                // Task 삭제하기
                binding.taskDeleteButton.setOnClickListener {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@TaskDetailActivity)
                    builder.setMessage("삭제하시겠습니까?")
                        .setTitle("계속할까요?")
                        .setNegativeButton("취소", null)
                        .setPositiveButton("삭제") { _, _ ->
//                            val resultIntent = Intent()
//                            resultIntent.putExtra(REMOVED_TASK_ID, currentTask!!.id)
//                            setResult(Activity.RESULT_OK, resultIntent)
                            database.child("tasks/${currentTask!!.id}").removeValue()
                            finish()
                        }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }

                if (binding.taskDetailRecyclerView.adapter == null) {
                    // create adapter
                    taskStepAdapter = TaskStepAdapter(currentTask!!)
                    taskDateAdapter = TaskDateAdapter(currentTask!!)
                    taskFileAdapter = TaskFileAdapter(currentTask!!,
                        { captureCamera() },
                        { filepath -> openImage(filepath) },
                        { addFileIntent() })

                    binding.taskDetailRecyclerView.adapter =
                        ConcatAdapter(taskStepAdapter, taskDateAdapter, taskFileAdapter)
                } else {
                    // update adapter
                    taskStepAdapter.update(currentTask!!)
                    taskDateAdapter.update(currentTask!!)
                    taskFileAdapter.update(currentTask!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (BuildConfig.DEBUG) {
                    error("task를 DB에서 가져오지 못함")
                }

                finish()
            }
        }
        database.child("tasks/$currentTaskId").addValueEventListener(taskListener)
    }

    override fun onStop() {
        binding.taskDetailRecyclerView.clearFocus()
        binding.taskDetailRecyclerView.clearFocus()
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.taskDetailRecyclerView.clearFocus()
        binding.taskNameText.clearFocus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            //after capture
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    if (resultCode == RESULT_OK) {
                        val file: File = File(mCurrentPhotoPath)
                        val bitmap = MediaStore.Images.Media
                            .getBitmap(contentResolver, Uri.fromFile(file))

                        if (bitmap != null) {
                            val ei = ExifInterface(mCurrentPhotoPath)
                            val orientation: Int = ei.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED
                            )

                            var rotatedBitmap: Bitmap? = null
                            when (orientation) {
                                ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap =
                                    rotateImage(bitmap, 90.0f)
                                ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap =
                                    rotateImage(bitmap, 180.0f)
                                ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap =
                                    rotateImage(bitmap, 270.0f)
                                ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
                                else -> rotatedBitmap = bitmap
                            }

                            //Rotate한 bitmap을 ImageView에 저장
                            //ivCapture.setImageBitmap(rotatedBitmap)
                            rotatedBitmap?.also {
                                saveImage(it)
                            }
                        }
                    }
                }
                REQUEST_TAKE_FILE -> {
                    if (resultCode == RESULT_OK) {
                        var dataUri = data?.data

//                        // 리사이클러뷰 아이템에 추가
//                        val taskFileAdapter: TaskFileAdapter =
//                            (binding.taskDetailRecyclerView.adapter as ConcatAdapter).adapters[2] as TaskFileAdapter
//
//                        val filepath = dataUri.toString()
//
//                        taskFileAdapter.fileList.add(filepath)
//                        taskFileAdapter.notifyDataSetChanged()

                        // upload
                        val filepath = dataUri.toString()

                        val timeStamp =
                            SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().time)
                        var imageFileName = "IMAGE_" + timeStamp + "_.png"

                        val storageRef = Firebase.storage.reference.child("${Firebase.auth.currentUser.uid}/images")
                            .child(imageFileName)

                        storageRef.putFile(dataUri!!).addOnSuccessListener {
                            Toast.makeText(this, "파일이 업로드되었습니다.", Toast.LENGTH_SHORT).show()

                            // 리사이클러뷰 아이템에 추가
                            val taskFileAdapter: TaskFileAdapter =
                                (binding.taskDetailRecyclerView.adapter as ConcatAdapter).adapters[2] as TaskFileAdapter

                            taskFileAdapter.fileList.add(filepath)
                            taskFileAdapter.notifyDataSetChanged()

                            Firebase.database.reference.child("tasks/${currentTask!!.id}/fileList").setValue(taskFileAdapter.fileList)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("waring", "onActivityResult Error !", e)
        }
    }

    fun captureCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                REQUEST_TAKE_PHOTO
            )
            return
        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // 인텐트를 처리 할 카메라 액티비티가 있는지 확인
        if (takePictureIntent.resolveActivity(packageManager) != null) {

            // 촬영한 사진을 저장할 파일 생성
            var photoFile: File? = null
            try {
                //임시로 사용할 파일이므로 경로는 캐시폴더로
                val tempDir: File = cacheDir

                //임시촬영파일 세팅
                val timeStamp: String = SimpleDateFormat("yyyyMMdd").format(Date())
                val imageFileName = "Capture_" + timeStamp + "_" //ex) Capture_20201206_
                val tempImage: File = File.createTempFile(
                    imageFileName,  /* 파일이름 */
                    ".jpg",  /* 파일형식 */
                    tempDir /* 경로 */
                )

                this.filename = tempImage.name

                // ACTION_VIEW 인텐트를 사용할 경로 (임시파일의 경로)
                mCurrentPhotoPath = tempImage.getAbsolutePath()
                photoFile = tempImage
            } catch (e: IOException) {
                //에러 로그는 이렇게 관리하는 편이 좋다.
                Log.w("warning", "파일 생성 에러!", e)
            }

            //파일이 정상적으로 생성되었다면 계속 진행
            if (photoFile != null) {
                //Uri 가져오기
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "$packageName.fileprovider",
                    photoFile
                )
                //인텐트에 Uri담기
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                //인텐트 실행
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    //카메라에 맞게 이미지 로테이션
    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun saveImage(bitmap: Bitmap) {
        try {
            //저장할 파일 경로
            val storageDir: File = getExternalFilesDir(null) ?: return
            Log.d("waring", "PATH: ${storageDir.path}")
            if (!storageDir.exists()) //폴더가 없으면 생성.
                storageDir.mkdirs()

            val file = File(storageDir, filename)
            val deleted = file.delete()
            Log.w("waring", "Delete Dup Check : $deleted")
            var output: FileOutputStream? = null
            try {
                output = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output) //해상도에 맞추어 Compress
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    assert(output != null)
                    output?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            Log.e("waring", "Captured Saved")
            Toast.makeText(this, "Capture Saved ", Toast.LENGTH_SHORT).show()


            var fileUri = Uri.fromFile(file)

            val storageRef = Firebase.storage.reference.child("${Firebase.auth.currentUser.uid}/images")
                .child(filename)

            storageRef.putFile(fileUri).addOnSuccessListener {
                Toast.makeText(this, "파일이 업로드되었습니다.", Toast.LENGTH_SHORT).show()

                // 리사이클러뷰 아이템에 추가
                val taskFileAdapter: TaskFileAdapter =
                    (binding.taskDetailRecyclerView.adapter as ConcatAdapter).adapters[2] as TaskFileAdapter

                taskFileAdapter.fileList.add("${storageDir.path}/$filename")

                taskFileAdapter.notifyDataSetChanged()

                Firebase.database.reference.child("tasks/${currentTask!!.id}/fileList").setValue(taskFileAdapter.fileList)
            }


        } catch (e: java.lang.Exception) {
            Log.w("waring", "Capture Saving Error!", e)
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun openImage(filepath: String) {
        intent = Intent(Intent.ACTION_VIEW)
        if (filepath.endsWith("jpg") || filepath.endsWith("jpeg") ||
            filepath.endsWith("JPG") || filepath.endsWith("gif") ||
            filepath.endsWith("png") || filepath.endsWith("bmp")
        ) {
            Timber.i("openImage")
            val data = Uri.parse(filepath)
            intent.setDataAndType(data, "image/*")
            startActivity(intent)
            Timber.i(filepath)
        }
    }

    private fun addFileIntent() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                REQUEST_TAKE_FILE
//            )
//            return
//        }

//        Intent(Intent.ACTION_VIEW)?.also {
//            var intent = Intent(Intent.ACTION_PICK)
//            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            intent.type = "image/*"
//            startActivityForResult(intent, REQUEST_TAKE_FILE)
//        }

        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQUEST_TAKE_FILE)
    }

    // 권한 요청
    // 카메라 권한 요청
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
            REQUEST_TAKE_PHOTO
        )
    }

    // 카메라 권한 체크
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    // 권한요청 결과
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Permission: " + permissions[0] + "was " + grantResults[0] + "카메라 허가")
        } else {
            Log.d("TAG", "카메라 미허가")
        }
    }

    companion object {
        private const val PICKER_IMAGE_FROM_ALBUM = 0
        private const val REQUEST_TAKE_PHOTO = 1
        private const val REQUEST_TAKE_FILE = 2
    }
}
