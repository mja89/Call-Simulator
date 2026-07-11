package com.example.callsimulator

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.callsimulator.data.ContactEntity
import java.io.File

class ContactAdapter(private var contacts: List<ContactEntity>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivContactImage)
        val name: TextView = view.findViewById(R.id.tvName)
        val phone: TextView = view.findViewById(R.id.tvPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        // از همان لایوت item_contact که ساختیم استفاده می‌کنیم
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        
        holder.name.text = contact.name
        holder.phone.text = contact.phoneNumber
        holder.image.clipToOutline = true
        holder.image.background = context.getDrawable(R.drawable.circle_shape) // از همان شیپی که ساختی استفاده کن

        // نمایش صحیح عکس از مسیر فایل محلی
        if (!contact.profileImageUri.isNullOrEmpty()) {
            val file = File(contact.profileImageUri)
            if (file.exists()) {
                holder.image.setImageURI(Uri.fromFile(file))
            } else {
                // اگر فایل به هر دلیلی پیدا نشد، تصویر پیش‌فرض نمایش داده شود
                holder.image.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        } else {
            // اگر آدرس خالی بود، تصویر پیش‌فرض نمایش داده شود
            holder.image.setImageResource(android.R.drawable.sym_def_app_icon)
        }
    }

    override fun getItemCount() = contacts.size

    // متد برای به‌روزرسانی لیست وقتی دیتابیس تغییر می‌کند
    fun updateList(newList: List<ContactEntity>) {
        this.contacts = newList
        notifyDataSetChanged()
    }
    }
    
