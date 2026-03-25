package com.example.a1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.BarcodeFormat
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
    private var bottomSheetDialog: BottomSheetDialog? = null

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
                val imgQr = view.findViewById<ImageView>(R.id.imgCartQr)
                val btnCopy = view.findViewById<Button>(R.id.btnCartCopy)
                val btnRemove = view.findViewById<Button>(R.id.btnCartRemove)

                txtName.text = coupon.productName
                txtCode.text = "Код: ${coupon.promoCode}"

                // Генерация маленького QR для списка
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        val bitMatrix = MultiFormatWriter().encode(coupon.qrData, BarcodeFormat.QR_CODE, 200, 200)
                        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565)
                        for (x in 0 until 200) {
                            for (y in 0 until 200) {
                                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                            }
                        }
                        withContext(Dispatchers.Main) {
                            imgQr.setImageBitmap(bitmap)
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }

                // Кнопка "Показать и копировать" -> Открывает большую панель
                btnCopy.text = "Показать" // Меняем текст кнопки
                btnCopy.setOnClickListener {
                    showQrBottomSheet(coupon.promoCode, coupon.productName, isFromCart = true)
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

    // --- ФУНКЦИЯ ПОКАЗА БОЛЬШОЙ ПАНЕЛИ С QR (КАК В КАТАЛОГЕ) ---
    private fun showQrBottomSheet(promoCode: String, dishName: String, isFromCart: Boolean) {
        bottomSheetDialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_qr, null)

        val qrImage = sheetView.findViewById<ImageView>(R.id.qrCodeImage)
        val promoText = sheetView.findViewById<TextView>(R.id.promoCodeText)
        val btnClose = sheetView.findViewById<Button>(R.id.btnCloseQr)

        promoText.text = promoCode
        btnClose.text = if (isFromCart) "Скопировать и закрыть" else "Закрыть"

        // Генерация большого QR кода
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                    promoCode,
                    BarcodeFormat.QR_CODE,
                    500, 500
                )
                val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565)
                for (x in 0 until 500) {
                    for (y in 0 until 500) {
                        bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                    }
                }

                withContext(Dispatchers.Main) {
                    qrImage.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CartActivity, "Ошибка генерации QR", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnClose.setOnClickListener {
            // Копируем код
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Промокод", promoCode)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Промокод скопирован!", Toast.LENGTH_SHORT).show()

            bottomSheetDialog?.dismiss()

            // Если мы не из корзины (а из каталога), то еще и сбрасываем счетчик/закрываем активность
            if (!isFromCart) {
                val resultIntent = Intent()
                resultIntent.putExtra("shouldReset", true)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

        bottomSheetDialog?.setContentView(sheetView)
        bottomSheetDialog?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomSheetDialog?.dismiss()
    }
}