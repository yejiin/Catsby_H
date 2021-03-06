/*
package org.techtown.catsby.setting


import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.fragment_set_location.*
import kotlinx.android.synthetic.main.fragment_setting.*
import org.techtown.catsby.R
import org.techtown.catsby.retrofit.dto.CatProfile
import org.techtown.catsby.retrofit.dto.User
import org.techtown.catsby.retrofit.service.CatService
import retrofit2.Call

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var fusedLocationClient:FusedLocationProviderClient
/**
 * A simple [Fragment] subclass.
 * Use the [setLocation.newInstance] factory method to
 * create an instance of this fragment.
 */
class setLocation : Fragment() {
    var locationClient: FusedLocationProviderClient? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    public var myadd : String?=null

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

        //???????????? ??????
        return inflater.inflate(R.layout.fragment_set_location, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val geocoder = Geocoder(context)

        //?????? ??????
        val userid = FirebaseAuth.getInstance().currentUser

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission( requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
        } else {
            myposbtn.setOnClickListener {
                requestLocation()
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    // myadd = address[0].subLocality
                    mypostv.setText(myadd)

                    // ??? ?????????(bundle) ?????? - bundle??? ???????????? ????????? ???????????? ?????????.
                    val bundle = bundleOf("myaddkey" to myadd)
                    // ???????????? ???????????? ???????????? ?????? ??????
                    setFragmentResult("myaddkey", bundle)

                    //val retrofitService: User = retrofit.create(UserService::class.java)
                    //val call: Call<User> = retrofitService.putUser(userid, myadd)



                }

            }}
        /*
            AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.LOCATION)
                .onGranted { permissions ->
                    Log.d("Main", "????????? ?????? ?????? : ${permissions.size}")
                }
                .onDenied { permissions ->
                    Log.d("main", "????????? ?????? ?????? : ${permissions.size}")
                }
                .start()


         */

    }

    private fun requestLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(activity)

        try{
            locationClient?.lastLocation?.addOnSuccessListener { location ->
                if(location == null) {
                    mypostv.setText("?????? ?????? ?????? ??????")
                } else {
                    //mypostv.setText("?????? ?????? ?????? ?????? : ${location.latitude}, ${location.longitude}")
                }
            }
                ?.addOnFailureListener{
                    mypostv.setText("?????? ?????? ?????? ??? ?????? : ${it.message}")
                    it.printStackTrace()
                }
            val locationRequest = LocationRequest.create()
            locationRequest.run{
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 60*1000
            }
            val locationCallback = object : LocationCallback(){
                override fun onLocationResult(p0: LocationResult?) {
                    p0?.let{
                        for((i, location) in it.locations.withIndex()) {
                            //mypostv.setText("??? ?????? : ${location.latitude}, ${location.longitude}" )
                        }
                    }
                }
            }
            //location ?????? ??? ??????
            locationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
        catch(e : SecurityException){
            e.printStackTrace()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment setLocation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            setLocation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

 */