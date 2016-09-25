#!/usr/bin python3.4
import serial
from time import sleep
from firebase import firebase
from datetime import datetime
import json
from pyfcm import FCMNotification
 
def bluetoothFunc (): # 블루투스 통신을 통해 tag 값, 이름값을 받아오는 함수
    print('hello')
    tag = bluetoothSerial.readline() # tag 값 수신
    tag = tag.decode("utf-8", "ignore")
    name = bluetoothSerial.readline() # 이름값 수신
    name = name.decode("utf-8", "ignore")
    tagList = tag.splitlines()
    nameList = name.splitlines()
    print(tagList[0])
    print(nameList[0])
    dt = datetime.now() # 현재 시각 저장
    data = {'date': dt.ctime(), 'tag': tagList[0], 'name': nameList[0]}
    result = firebase.post('test', data) # firebase에 data 내용 POST
    print (result)
 
def fcm () : # Firebase Cloud Messaging을 위한 함수
    push_service = FCMNotification(api_key="AIzaSyDwvtqW_1uQnWDLzjqP2U_MA_4_ogRdeZ4")
    registration_id = "fzLriiEr0bo:APA91bG2kG8pHyig3yOl3l9jmucHPIcNR1xKJZ2a_CzX1KJqKh5tXOe0sfXCi__U2p6n4hKgEpd6CxPQb_DcdRzaMWF0N4WGi5JCBSRckgf2Ba_CVYdDMOOt-59GaIkxbc6sBZsrlXaC"
    registration_ids =  ["fzLriiEr0bo:APA91bG2kG8pHyig3yOl3l9jmucHPIcNR1xKJZ2a_CzX1KJqKh5tXOe0sfXCi__U2p6n4hKgEpd6CxPQb_DcdRzaMWF0N4WGi5JCBSRckgf2Ba_CVYdDMOOt-59GaIkxbc6sBZsrlXaC",
                    "dVKg3MEBuo8:APA91bF3tJM3L2A5qtFal17SynmsxfDrLixrEcAKr0NhLo1ThYW-hq7wtQtoBSNUiozEzC6s8hJ_ZsAJs7YwCjRgggPslvSPUwKWxK9sEs5JTWefp_EUmjZVo-ArFiY5H93n68Wi7qMH",
		    "cXZnYQmQdO0:APA91bF_RdrhM7Q_1iB5CNfHicLbfYq4BWhaMpo2iKgU_AmI-vQ2X1STqnfLDv9AWWmJSOtdSPcBTmXJ8z9Od8deY9_BLw7VQePrm95eFp3B2fnqD2lzK3M36XzyYRbTLDrS9STFGkeK"]
 # 메시지를 수신 받을 기기의 토큰

    data_message = {
        "title" : "Title",
        "message" : "Test"
    }
# 전송할 메시지 내용
    
    result = push_service.notify_multiple_devices(registration_ids=registration_ids, data_message=data_message)
# 다수의 기기에 푸시 메시지를 전송
    
 
bluetoothSerial = serial.Serial( "/dev/rfcomm1", baudrate=9600 )
# 블루투스 통신을 위한 시리얼 연결
 
firebase = firebase.FirebaseApplication('https://fcm-test-1321e.firebaseio.com/', None)
 
while 1: # 블루투스 통신과 클라우드 메시징 서비스를 계속해서 시도함
    try:
        bluetoothFunc()
        fcm()
    except:
        pass    # Ignore any errors that may occur and try again