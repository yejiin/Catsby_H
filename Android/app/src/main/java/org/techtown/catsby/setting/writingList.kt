package com.example.catsbe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_writing_list.*
import org.techtown.catsby.R
import org.techtown.catsby.retrofit.dto.MyComment
import org.techtown.catsby.retrofit.dto.MyPost
import org.techtown.catsby.retrofit.service.MyWritingService
import org.techtown.catsby.setting.Comment
import org.techtown.catsby.setting.CommentAdapter
import org.techtown.catsby.setting.Writing
import org.techtown.catsby.setting.WritingListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [writingList.newInstance] factory method to
 * create an instance of this fragment.
 */
class writingList : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_writing_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView1.layoutManager = LinearLayoutManager(context)
        val adapter1 = WritingListAdapter()

        val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service1 = retrofit.create(MyWritingService::class.java)

        val call = service1.myPosts

        call.enqueue(object : Callback<List<MyPost>> {
            override fun onResponse(call: Call<List<MyPost>>, response: Response<List<MyPost>>) {
                if (response.isSuccessful) {
                    //정상적으로 통신이 성공된 경우

                    val result: List<MyPost>? = response.body()

                    if (result != null) {
                        for (i in 0..result.size-1){
                            if(result.get(i).townCommunity == null)
                                adapter1.items.add(Writing(result.get(i).bowlCommunity.content,result.get(i).bowlCommunity.updateDate))

                            else if(result.get(i).bowlCommunity == null)
                                adapter1.items.add(Writing(result.get(i).townCommunity.title,result.get(i).townCommunity.date))
                        }
                    }
                    recyclerView1.adapter = adapter1

                } else {
                    println("실패!")
                }
            }

            override fun onFailure(call: Call<List<MyPost>>, t: Throwable) {
                println("통신 실패! $t")
            }
        })

        //내 글 ui ex
//        adapter1.items.add(Writing("안녕하세요","21-06-06"))
//        adapter1.items.add(Writing("저희 집 고양이가","21-06-09"))
//        adapter1.items.add(Writing("동물 병원 추천해주세요","21-07-06"))

        //내 글 클릭 했을 때 글 페이지로 이어지게 하려면 0406-56:41

        writingbtn.setOnClickListener {
            recyclerView1.adapter = adapter1
        }

        commentbtn.setOnClickListener {
            recyclerView1.layoutManager = LinearLayoutManager(context)
            val adapter2 = CommentAdapter()

            val retrofit = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val service1 = retrofit.create(MyWritingService::class.java)

            val call = service1.myComments

            call.enqueue(object : Callback<List<MyComment>> {
                override fun onResponse(call: Call<List<MyComment>>, response: Response<List<MyComment>>) {
                    if (response.isSuccessful) {
                        //정상적으로 통신이 성공된 경우

                        val result: List<MyComment>? = response.body()

                        if (result != null) {
                            for (i in 0..result.size-1){
                                if(result.get(i).townComment == null)
                                    adapter2.items.add(Comment(result.get(i).bowlComment.content,result.get(i).bowlComment.bowlCommunity.content))

                                else if(result.get(i).bowlComment == null)
                                    adapter2.items.add(Comment(result.get(i).townComment.content,result.get(i).townComment.townCommunity.title))
                            }
                        }
                        recyclerView1.adapter = adapter2

                    } else {
                        println("실패!")
                    }
                }

                override fun onFailure(call: Call<List<MyComment>>, t: Throwable) {
                    println("통신 실패! $t")
                }
            })
            //내 댓글 ui ex
//            adapter2.items.add(Comment("너무 귀여워요", "저희 집 고양이좀 보고가세요"))
//            adapter2.items.add(Comment("탐돌이가 좋을 것 같네요", "고양이 이름 지어주세요"))
//            adapter2.items.add(Comment("8kg 정도 돼보여요", "몇키로 정도 돼보이나요? 이 정도면 뚱냥인가요"))
//            adapter2.items.add(Comment("뚱냥이 절대 아니에요", "몇키로 정도 돼보이나요? 이 정도면 뚱냥인가요"))
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment writingList.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                writingList().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}