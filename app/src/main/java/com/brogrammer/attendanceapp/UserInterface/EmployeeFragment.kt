package com.brogrammer.imageclassificationlivefeed.UserInterface

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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


class EmployeeFragment : Fragment() {

    private lateinit var customDialogTimeStampsReport: CustomDialogTimeStampsReport

    private lateinit var totalPresentTV : TextView
    private lateinit var totalAbsentTV : TextView

    private var selectedDateDlg: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_employee,container,false)

        // Initialize CustomDialogTimeStampsReport
        customDialogTimeStampsReport = CustomDialogTimeStampsReport(requireActivity(),"","")

        selectedDateDlg = getCurrentDateInDesiredFormatDlg()
        val currentDate  = getCurrentDateInDesiredFormat()

//        val currentDate = 20240208


        //Find the dateTextView in the fragment layout
        totalPresentTV = view.findViewById(R.id.totalPresent)
        totalAbsentTV = view.findViewById(R.id.totalAbsent)


        val employeeFragmentConstraintLayout = view.findViewById<ConstraintLayout>(R.id.employeeFragmentConstraintLayout)
        employeeFragmentConstraintLayout.visibility = View.GONE

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Custom loading dialog
        val loadingDialog = LoadingDialog(requireActivity())


        recyclerView.layoutManager = LinearLayoutManager(activity)


        val divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                divider?.let { setDrawable(it) }
            }
        )

        // show the loading dialog
        loadingDialog.startLoading()

        DataManager.attendanceReportFromMongo(currentDate, object : com.brogrammer.imageclassificationlivefeed.BackEnd.ReportCallback {
            override fun onReportReady(presentCount: Int, absentCount: Int) {
                // Update the UI on the main thread
                activity?.runOnUiThread {
                    totalPresentTV.text = presentCount.toString()
                    totalAbsentTV.text = absentCount.toString()

                    // Update the RecyclerView here, after the data is ready
                    val itemList = DataManager.getItemList()
                    val adapter = MyAdapter(itemList,customDialogTimeStampsReport,selectedDateDlg,currentDate)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()

                    // Dismiss the loading dialog when your task is complete
                    loadingDialog.dismiss()

                    employeeFragmentConstraintLayout.visibility = View.VISIBLE

                }
            }
        })

        return view
    }

    fun getCurrentDateInDesiredFormat(): Int {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR) // YYYY
        val month = cal.get(Calendar.MONTH) + 1 // Month starts from 0, so add 1 (MM)
        val day = cal.get(Calendar.DAY_OF_MONTH) // DD
        return year * 10000 + month * 100 + day
    }

    // 12 Feb,2024
    fun getCurrentDateInDesiredFormatDlg(): String {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return dateFormat.format(cal.time)
    }

}
