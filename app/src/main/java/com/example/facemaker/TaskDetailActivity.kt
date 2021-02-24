package com.example.facemaker

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val REMOVED_TASK_ID = "removedTaskId"

class TaskDetailActivity : AppCompatActivity() {
    val REQUEST_TAKE_PHOTO = 1
    var mCurrentPhotoPath: String = ""
    var filename: String = ""
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        /* Connect variables to UI elements. */
        val taskContent: EditText = findViewById(R.id.task_detail_content)
        val taskCheckBox: CheckBox = findViewById(R.id.task_detail_checkBox)

        val bundle: Bundle? = intent.extras
        val currentTaskId: Int = bundle?.getInt(TASK_ID) ?: return
        val currentTask = ProjectManager.getTaskForId(currentTaskId) ?: return

        taskContent.setText(currentTask.content, TextView.BufferType.EDITABLE)
        taskCheckBox.isChecked = currentTask.isDone

        // 체크박스 클릭했을 때 이벤트 추가
        val checkBox: CheckBox = findViewById(R.id.task_detail_checkBox)
        checkBox.setOnClickListener {
            currentTask.isDone = checkBox.isChecked
            ProjectManager.save(filesDir)
        }

        // EditText focus 가 변경될 때 이벤트 추가
        /*val editText: EditText = findViewById(R.id.task_detail_content)
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                currentTask.content = editText.text.toString()
            }
        }*/

        // 변경될 때마다가 아니라 초점을 잃었을 때만 변경해야하지만
        // 임시로 EditText 가 변경될 때 이벤트 추가
        val editText: EditText = findViewById(R.id.task_detail_content)
        editText.addTextChangedListener {
            currentTask.content = editText.text.toString()
        }

        // Task 삭제하기
        val deleteButton: ImageButton = findViewById(R.id.task_detail_delete_button)
        deleteButton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("삭제하시겠습니까?")
                .setTitle("계속할까요?")
                .setNegativeButton("취소", null)
                .setPositiveButton("삭제") { _, _ ->
                    val resultIntent = Intent()
                    resultIntent.putExtra(REMOVED_TASK_ID, currentTaskId)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        val taskDateAdapter = TaskDateAdapter(currentTask)
        val taskFileAdapter = TaskFileAdapter(currentTask) { captureCamera()}

        recyclerView = findViewById<RecyclerView>(R.id.task_detail_recycler_view)
        recyclerView.adapter = ConcatAdapter(taskDateAdapter, taskFileAdapter)
    }

    override fun onStop() {
        ProjectManager.save(filesDir)
        super.onStop()
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

//                            //사진해상도가 너무 높으면 비트맵으로 로딩
//                            BitmapFactory.Options options = new BitmapFactory.Options();
//                            options.inSampleSize = 8; //8분의 1크기로 비트맵 객체 생성
//                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
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
            }
        } catch (e: Exception) {
            Log.w("waring", "onActivityResult Error !", e)
        }
    }

    // 카메라, 저장소 접근 권한 요청 추가할 것
    fun captureCamera() {
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

            // 리사이클러뷰 아이템에 추가
            val taskFileAdapter: TaskFileAdapter =
                (recyclerView.adapter as ConcatAdapter).adapters[1] as TaskFileAdapter

            taskFileAdapter.fileList.add(this.filename)
            taskFileAdapter.notifyDataSetChanged()

        } catch (e: java.lang.Exception) {
            Log.w("waring", "Capture Saving Error!", e)
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun openImage() {
        intent = Intent(Intent.ACTION_VIEW)
        if (this.filename.endsWith("jpg") || this.filename.endsWith("jpeg") ||
            this.filename.endsWith("JPG") || this.filename.endsWith("gif") ||
            this.filename.endsWith("png") || this.filename.endsWith("bmp")
        ) {
            val storageDir: File = getExternalFilesDir(null) ?: return
            val data =
                Uri.parse("${storageDir.path}/${this.filename}")
            intent.setDataAndType(data, "image/*")
            //startActivityForResult(intent, 3)
            startActivity(intent)
        }
    }
}
