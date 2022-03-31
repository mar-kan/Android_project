# Android_project

The purpose of this project is the development of an Environmental Management system which focuses on the safety of citizens in emergancy situations. The following figure shows the structural components of the project's architecture.


![Screenshot from 2022-03-31 22-54-54](https://user-images.githubusercontent.com/62807134/161138812-ccd9aa38-789d-4aa7-b58c-6d5da5244a6d.png)


---------------------------

## Android Device

It is the mobile device of a user which is used to receive notifications in case of an emergency. It is connected to the server via the MQTT protocol, receives from the server notifications related to the environmental conditions and sends a GPS signal to the server that can be automatically or manually set.

---------------------------

## IoT Devices

These are two devives placed in the environment which include embedded sensors and are used to collect measurements and send them to the server. They are connected to the server via the MQTT protocol and periodically send to it their measurements, along with their GPS signal and their battery percentage.

----------------------------

## Edge Server

The Edge Server receives data from the Android and IoT devices and notifies the android devices in case of an emergency. Every emergency that is detected is stored in an SQL database.

----------------------------

## MySQL Database

Every emergency that is detected from the server is stored in a database, which is connected to the server via JDBC driver.

