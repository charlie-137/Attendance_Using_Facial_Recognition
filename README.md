# Attendance Using Facial Recognition

This project aims to revolutionize traditional employee attendance tracking methods by leveraging cutting-edge facial recognition technology. 
This innovative Android application streamlines the attendance process, allowing employees to effortlessly clock in and out by simply presenting their faces to a camera. 
By harnessing the power of facial recognition, the project enhances efficiency, accuracy, and security in workforce management, paving the way for a modernized approach to attendance tracking.


## Features
A.) **Face Registration**: In the face registration phase, we systematically register faces, enabling our system to recognize them in subsequent interactions. 
This process involves capturing and storing facial data, creating a comprehensive database of registered individuals.  
  
B.) **Face Recognition**: In the face recognition segment, our system leverages the registered faces, employing advanced algorithms and machine learning models 
to accurately and swiftly identify individuals. This feature plays a pivotal role in enhancing security, user experience, and operational efficiency. 
The recognized faces are matched against the registered profiles, facilitating access control, attendance tracking, and more.  
  
C.) **Attendance Tracking**: This segment encompasses various functionalities for tracking and monitoring employee attendance:  
- Individual Attendance Tracking  
- Group Attendance Status  
- Current Date Attendance History  

## Screenshot

|                                                                                                                          |                                                                                                                           |
|--------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| ![dashboard_ss](https://github.com/charlie-137/Attendance_Using_Facial_Recognition/blob/master/assets/dashboard.png)         | ![add_employee_face_ss](https://github.com/charlie-137/Attendance_Using_Facial_Recognition/blob/master/assets/add_employee_face.png) |
| ![add_face_dialog_ss](https://github.com/charlie-137/Attendance_Using_Facial_Recognition/blob/master/assets/add_face_dialog.png) | ![attendance_marked_ss](https://github.com/charlie-137/Attendance_Using_Facial_Recognition/blob/master/assets/attendance_marked.png)       |
| ![selected_date_attendance_report_ss](https://github.com/charlie-137/Attendance_Using_Facial_Recognition/blob/master/assets/selected_date_attendance_report.png)         | ![employee_wise_attendance_report_ss](https://github.com/charlie-137/Attendance_Using_Facial_Recognition/blob/master/assets/employee_wise_attendance_report.png) |
| ![current_date_attendance_report_ss](https://github.com/charlie-137/Attendance_Using_Facial_Recognition/blob/master/assets/current_date_attendance_report.png) | ![attendance_timestamps_ss](https://github.com/charlie-137/Attendance_Using_Facial_Recognition/blob/master/assets/attendance_timestamps.png)       |


## Build With

[Kotlin](https://kotlinlang.org/), [Java](https://developer.android.com/codelabs/build-your-first-android-app#0):
As the programming language.

[XML](https://developer.android.com/reference/android/util/Xml) :
To build UI.

[ML KIT](https://developers.google.com/ml-kit/vision/face-detection/android) :
For Facial Detection.

[Mobile FaceNet Model](https://medium.com/gravel-engineering/recognizing-face-in-android-using-deep-neural-network-tensorflow-lite-be980efea656) :
For Facial Recognition.

[Volley Library](https://google.github.io/volley/) :
To performt he HTTP request and fetch the data from the database

[Realm MongoDB](https://www.mongodb.com/docs/atlas/device-sdks/sdk/java/install/) :
For storing the Facial Embeddings and the Attendance History.


## Architecture

This app follows MVVM architecture
pattern.
  
![flow_architecture](https://github.com/charlie-137/Attendance_Using_Facial_Recognition/blob/master/assets/architecure_diagram.jpg)
