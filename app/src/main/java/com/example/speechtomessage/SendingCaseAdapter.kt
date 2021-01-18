package com.example.speechtomessage


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class SendingCaseAdapter(listData: List<Message>, context: Context) : RecyclerView.Adapter<SendingCaseAdapter.TypesListHolder>() {

    private var inflater: LayoutInflater
    private var listData: List<Message>
    private var context : Context

    init {
        this.listData  =  listData
        this.context = context
        this.inflater = LayoutInflater.from(context)
    }

    override fun onBindViewHolder(holder: TypesListHolder, position: Int) {
        val item: Message = listData.get(position)
        val mTextContactName  = holder.itemView.findViewById(R.id.textContactName) as TextView
        val imageCase  = holder.itemView.findViewById(R.id.image_failure) as ImageView

        if (item != null) {
            mTextContactName.text = item.getContact()?.getDisplayName()

            if(item.getIsSended() == true)
                imageCase.setImageResource(R.drawable.tick_64px)
            else
                imageCase.setImageResource(R.drawable.close_64px)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):TypesListHolder {
        val view: View = inflater.inflate(R.layout.card_not_sended, parent, false)
        return TypesListHolder(view)
    }


    class TypesListHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private  var mTextContactName: TextView
        private  var mImageFailure :ImageView

        init{
            mTextContactName = itemView.findViewById(R.id.textContactName) as TextView
            mImageFailure = itemView.findViewById(R.id.image_failure) as ImageView
        }
    }

}