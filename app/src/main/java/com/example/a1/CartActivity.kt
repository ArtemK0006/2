package com.example.a1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var emptyText: TextView
    private val adapterItems = mutableListOf<CartCoupon>()
    private lateinit var arrayAdapter: ArrayAdapter<CartCoupon>
    private var currentDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        listView = findViewById(R.id.cartListView)
        emptyText = findViewById(R.id.emptyCartText)
        val btnBack = findViewById<Button>(R.id.btnBackCart)

        btnBack.setOnClickListener { finish() }

        // Адаптер для списка
        arrayAdapter = object : ArrayAdapter<CartCoupon>(this, android.R.layout.simple_list_item_1, adapterItems) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_cart_coupon, parent, false)
                val coupon = adapterItems[position]

                val txtName = view.findViewById<TextView>(R.id.txtCartName)
                val txtCode = view.findViewById<TextView>(R.id.txtCartCode)
                val imgProduct = view.findViewById<ImageView>(R.id.imgCartProduct)

                val btnShowQr = view.findViewById<Button>(R.id.btnCartCopy)
                val btnRemove = view.findViewById<Button>(R.id.btnCartRemove)

                txtName.text = coupon.productName
                txtCode.text = "Код: ${coupon.promoCode}"
                imgProduct.setImageResource(coupon.imageResId)

                btnShowQr.text = "Показать QR"
                btnShowQr.backgroundTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_blue_light))

                btnShowQr.setOnClickListener {
                    showLargeQrDialog(coupon)
                }

                btnRemove.setOnClickListener {
                    SecondActivity.cartItems.removeAt(position)
                    adapterItems.removeAt(position)
                    notifyDataSetChanged()
                    checkEmpty()
                    Toast.makeText(context, "Удалено", Toast.LENGTH_SHORT).show()
                }

                return view
            }
        }

        listView.adapter = arrayAdapter
        loadCart()
    }

    // Функция показа  QR из XML
    private fun showLargeQrDialog(coupon: CartCoupon) {
        currentDialog = BottomSheetDialog(this)

        // Загружаем красивый XML файл
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_qr, null)

        val qrImage = sheetView.findViewById<ImageView>(R.id.qrCodeImage)
        val promoText = sheetView.findViewById<TextView>(R.id.promoCodeText)
        val btnClose = sheetView.findViewById<Button>(R.id.btnCloseQr)

        // Устанавливаем текст промокода
        promoText.text = coupon.promoCode

        // Генерация QR кода
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val hints = mutableMapOf<EncodeHintType, Any>()
                hints[EncodeHintType.MARGIN] = 1 // Маленькие поля вокруг

                val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                    coupon.qrData,
                    BarcodeFormat.QR_CODE,
                    800, 800,
                    hints
                )

                val width = bitMatrix.width
                val height = bitMatrix.height
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }

                withContext(Dispatchers.Main) {
                    qrImage.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CartActivity, "Ошибка генерации QR", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Логика кнопки "Скопировать и закрыть"
        btnClose.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Промокод", coupon.promoCode)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Промокод скопирован!", Toast.LENGTH_SHORT).show()
            currentDialog?.dismiss()
        }

        currentDialog?.setContentView(sheetView)
        currentDialog?.show()
    }

    private fun loadCart() {
        adapterItems.clear()
        adapterItems.addAll(SecondActivity.cartItems)
        arrayAdapter.notifyDataSetChanged()
        checkEmpty()
    }

    private fun checkEmpty() {
        if (adapterItems.isEmpty()) {
            listView.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
        } else {
            listView.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentDialog?.dismiss()
    }
}