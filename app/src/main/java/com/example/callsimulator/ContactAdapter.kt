package com.example.callsimulator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.callsimulator.model.Contact

class ContactAdapter(
    private val contacts: List<Contact>,
    private val onCallClick: (Contact) -> Unit,
    private val onConfigClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val phone: TextView = view.findViewById(R.id.txtPhone)
        val voiceStatus: TextView = view.findViewById(R.id.txtVoiceStatus)
        val btnCall: android.widget.Button = view.findViewById(R.id.btnCall)
        val btnConfig: android.widget.Button = view.findViewById(R.id.btnConfig)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val contact = contacts[position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone
        val count = VoiceStorage.getVoicesFor(holder.itemView.context, contact.id).size
        holder.voiceStatus.text = if (count > 0) "$count وویس تنظیم شده" else "بدون وویس"
        holder.btnCall.setOnClickListener { onCallClick(contact) }
        holder.btnConfig.setOnClickListener { onConfigClick(contact) }
    }

    override fun getItemCount() = contacts.size
}
