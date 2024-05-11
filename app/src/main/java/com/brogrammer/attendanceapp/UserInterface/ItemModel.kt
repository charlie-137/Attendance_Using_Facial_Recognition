package com.brogrammer.attendanceapp.UserInterface

import android.graphics.Bitmap

data class ItemModel(val id:Int,
                     val empCode: String,
                     val empName: String,
                     var attendanceStatus: String,
                     var faceImgId: Bitmap? = null,
                     var employmentStatus:Int,
                     var attendanceEligibility:Int)


// id (Integer) -  It stores the auto-incremented unique id (1,2,3,4,......) for each employee
// empCode (String) - It stores the EmpCode of the Employee (if id = 1 then Emp Code = E001, if id=5 then Emp Code = E005)
// empName (String) - It stores the Employee Name
// attendanceStatus (String) - It stores the current Attendance status ('P' - Present and 'A' - Absent) (Default - 'A')
// faceImgId (Bitmap) - It stores the registered face of the employee
// employmentStatus (Int) - It Stores the current employment status of the employee (1 - Working and 0 - Not Working)
// attendanceEligibility (Int) - It stores whether the employee's attendance is to be marked or not (1 - True and 0 - False)

