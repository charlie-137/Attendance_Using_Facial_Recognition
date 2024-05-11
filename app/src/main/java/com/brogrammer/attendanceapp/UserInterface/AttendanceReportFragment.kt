package com.brogrammer.imageclassificationlivefeed.UserInterface


import android.os.Handler
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brogrammer.imageclassificationlivefeed.BackEnd.DataManager
import com.brogrammer.imageclassificationlivefeed.R
import com.brogrammer.imageclassificationlivefeed.UserInterface.LoadingDialog.CustomDialogTimeStampsReport
import com.brogrammer.imageclassificationlivefeed.UserInterface.LoadingDialog.LoadingDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//import java.util.logging.Handler


class AttendanceReportFragment : Fragment() {

    // custom dialog for displaying the attendance report in dialog
    private lateinit var customDialogTimeStampsReport: CustomDialogTimeStampsReport

    private lateinit var dateTextView: TextView
    private lateinit var loadReportBtn: Button
    private lateinit var totalPresentTV : TextView
    private lateinit var totalAbsentTV : TextView
    private lateinit var dateTV : TextView
    private lateinit var homeImageButton : ImageButton


    private var selectedDate: String? = null
    private var selectedDateDlg: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_attendance_report, container, false)

        // Initialize CustomDialogTimeStampsReport
        customDialogTimeStampsReport = CustomDialogTimeStampsReport(requireActivity(),"","")


        //Find the dateTextView in the fragment layout
        dateTextView = view.findViewById(R.id.datePickerTV)
        loadReportBtn = view.findViewById(R.id.loadAttnReportBtn)
        totalPresentTV = view.findViewById(R.id.totalPresent)
        totalAbsentTV = view.findViewById(R.id.totalAbsent)
        dateTV = view.findViewById(R.id.textView)
        homeImageButton = view.findViewById(R.id.homeImageButton)


        homeImageButton.setOnClickListener{
            val fragment = DashboardFragment()
            replaceFragment(fragment)
        }

        val attendanceReportConstraintLayout = view.findViewById<ConstraintLayout>(R.id.attendanceReportConstraintLayout)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Custom loading dialog
        val loadingDialog = LoadingDialog(requireActivity())


//        loadReportBtn.setOnClickListener{
//            val selectedDate = useSelectedDate()
//            if(selectedDate != null)
//            {
//                DataManager.attendanceReportFromMongo(selectedDate)
//                val present = DataManager.getPresentCount()
//                val absent = DataManager.getAbsentCount(present)
//                totalPresentTV.text = present.toString()
//                totalAbsentTV.text = absent.toString()
//            }
//        }


        // Initialize RecyclerView and its adapter outside the click listener
//        val itemList = DataManager.getItemList()

        recyclerView.layoutManager = LinearLayoutManager(activity)
//        val adapter = MyAdapter(itemList)
//        recyclerView.adapter = adapter

        val divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                divider?.let { setDrawable(it) }
            }
        )


        loadReportBtn.setOnClickListener{
            val selectedDate = useSelectedDate()
            if (selectedDate != null) {

            // To show the loading dialog
                loadingDialog.startLoading()

                DataManager.attendanceReportFromMongo(selectedDate, object : com.brogrammer.imageclassificationlivefeed.BackEnd.ReportCallback {
                    override fun onReportReady(presentCount: Int, absentCount: Int) {
                        // Update the UI on the main thread
                        activity?.runOnUiThread {
                            totalPresentTV.text = presentCount.toString()
                            totalAbsentTV.text = absentCount.toString()

                            // Update the RecyclerView here, after the data is ready
                            val itemList = DataManager.getItemList()

                            val adapter = MyAdapter(itemList,customDialogTimeStampsReport,selectedDateDlg,selectedDate)

                            recyclerView.adapter = adapter
                            adapter.notifyDataSetChanged()

                            // Dismiss the loading dialog when your task is complete
                            loadingDialog.dismiss()
                            attendanceReportConstraintLayout.visibility = View.VISIBLE
                        }
                    }
                })

            }


        }

        dateTextView.setOnClickListener{
          showDatePickerDialog()


            // Delayed action to hide attendanceReportConstraintLayout after 2 seconds
            Handler().postDelayed({
                attendanceReportConstraintLayout.visibility = View.GONE
            }, 50)


//            attendanceReportConstraintLayout.visibility = View.GONE
        }




//        val itemList = DataManager.getItemList()
//        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(activity)
//        val adapter = MyAdapter(itemList)
//        recyclerView.adapter = adapter
//        val divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
//        recyclerView.addItemDecoration(
//            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
//                divider?.let { setDrawable(it) }
//            }
//        )

        return view
    }



    private fun showDatePickerDialog()
    {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, day: Int ->
              val selectedDate = String.format("%02d/%02d/%04d",day,month+1,year)
                dateTextView.text = selectedDate

                val formattedDate = String.format("%04d%02d%02d",year,month+1,day)
                Log.d("FormattedDateLog",formattedDate)

                //Format date in "01 Feb, 2024" format
                val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                val parsedDate = inputFormat.parse(formattedDate)
                val formattedDateOutput = outputFormat.format(parsedDate!!)
                Log.d("FormattedDateOutput",formattedDateOutput)
                dateTV.text = "$formattedDateOutput - Report"
                selectedDateDlg = formattedDateOutput
                this.selectedDate =  formattedDate

            },
            year, month, day
        )
        datePickerDialog.show()

    }



    //Access the saved date whenever needed
    fun useSelectedDate(): Int? {
        if(selectedDate!=null)
        {
            Log.d("SelectedDateLog", selectedDate!!)
        }
        return selectedDate?.toInt()
    }

    private fun showCustomToast(message: String) {
        // Use the context of the fragment to display the Toast
        val context = requireContext()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }



}