/*
 # Product: NFC Module for Arduino
 # SKU    : DFR0231

 command list: 
 #wake up reader
 send: 55 55 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ff 03 fd d4 14 01 17 00
 return: 00 00 FF 00 FF 00 00 00 FF 02 FE D5 15 16 00

 #get firmware
 send: 00 00 FF 02 FE D4 02 2A 00
 return: 00 00 FF 00 FF 00 00 00 FF 06 FA D5 03 32 01 06 07 E8 00

 #read the tag
 send: 00 00 FF 04 FC D4 4A 01 00 E1 00
 return: 00 00 FF 00 FF 00 00 00 FF 0C F4 D5 4B 01 01 00 04 08 04 XX XX XX XX 5A 00
 XX is tag.
 mine:   00 00 FF 00 FF 00 00 00 FF 0C F4 D5 4B 01 01 00 04 08 04 07 8C FC 4F F0 00
 (In this program, I'll use XX XX XX XX 5A as tag ID)
 */

#define wake_result 15
#define firmware_result 19
#define tag_result 25

#include <Servo.h>
#include <SoftwareSerial.h>

const unsigned char wake[24]={
  0x55, 0x55, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, \
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xff, 0x03, 0xfd, 0xd4, 0x14, 0x01, 0x17, 0x00};//wake up NFC module
const unsigned char firmware[9]={
  0x00, 0x00, 0xFF, 0x02, 0xFE, 0xD4, 0x02, 0x2A, 0x00};//
const unsigned char tag[11]={
  0x00, 0x00, 0xFF, 0x04, 0xFC, 0xD4, 0x4A, 0x01, 0x00, 0xE1, 0x00};//detecting tag command
const unsigned char std_ACK[25] = {
  0x00, 0x00, 0xFF, 0x00, 0xFF, 0x00, 0x00, 0x00, 0xFF, 0x0C, \
0xF4, 0xD5, 0x4B, 0x01, 0x01, 0x00, 0x04, 0x08, 0x04, 0x00, 0x00, 0x00, 0x00, 0x4b, 0x00};
unsigned char old_id[5];
unsigned char cur_id[5];

String cardid ="0";
String who="WOW";    //등록된 NFC 카드가 태깅되었을 때

unsigned char receive_ACK[25];//Command receiving buffer

int pos = 0;    // variable to store the servo position
int sensorPin = A0;
int val;
int buttonPin = 6;
byte leds = 0;

String family;

int mark=0;
int check=0;

SoftwareSerial BTSerial(2, 3);

Servo myservo;  // create servo object to control a servo
// twelve servo objects can be created on most boards

void setup()
{
   myservo.attach(9);  // attaches the servo on pin 9 to the servo object
   
  Serial.begin(115200);   // open serial with PC
  delay(100);
  BTSerial.begin(9600);
  Serial.println("Hello!");
  Serial.println("wake card");
  delay(100);  // give delay before sending command bytes
  wake_card();
  delay(100);
  read_ACK(wake_result);
  delay(100);
  display(wake_result);

  firmware_version();
  delay(100);
  read_ACK(firmware_result);
  delay(100);
  display(firmware_result);

  pinMode(8, INPUT_PULLUP);

  pinMode(buttonPin, INPUT_PULLUP);
}

// scan NFC tag every second
void loop()
{
  send_tag();
  read_ACK(tag_result);
  read_id();
  
  if (!no_detection()) {       //NFC 카드 태그했을 경우
    print_id();

    if((mark==1)&&(check==1))
    {
      who = "no";
      check=0;
      mark=0;
      }
  }
  
  delay(1000);

  if((cardid=="4906424277"||cardid=="4657524277"||cardid=="41207424277"||cardid=="4718824277"||cardid=="1")&&(check!=1))
  {
      Serial.println("WOW");
      who = "WOW";
      if(cardid=="4906424277")
      {    
          family = "mom";
          check=1;
      }
     else if(cardid=="4657524277")
     {
         family = "dad";
         check=1;
      }
     else if(cardid=="41207424277")
     {
        family = "sister";
        check=1;
      }
      else if(cardid=="4718824277")
      {
         family = "brother";
         check=1;
      }
     
      
      if(!no_detection())
      {
          who_family();
      }
    
  }
  else
  {
      Serial.println("no");
      who = "no";
  }

  
  val = digitalRead(8);
  Serial.println(val);
  delay(100);
  
  if(who=="WOW")
  {
      myservo.write(0);              // tell servo to go to position in variable 'pos'
      delay(1000);
    
      if((val==0)&&(mark!=1))
      {
        delay(5000);
        cardid="0";
        check=0;
      }
  }
  else if(who=="no")
  {

    if(cardid=="1")
    {
      mark=1;
      check=0;
      }
      else{
    myservo.write(90);              // tell servo to go to position in variable 'pos'
    delay(15); 
      }
  }

  //delay(1000);
  
 // BT –> Data –> Serial
  if (BTSerial.available()) {
    //Serial.flush();
    Serial.write(BTSerial.read());
  }
  // Serial –> Data –> BT
  if (Serial.available()) {
    //BTSerial.println("chicken\n");
    //BTSerial.println(val);           //문열림닫힘: int val; (닫힘: 0, 열림: 1)
   // BTSerial.println(cardid);        //카드아이디: String cardid;
  // BTSerial.println(family);
  }

  if(digitalRead(buttonPin)==LOW)    //버튼이 눌렸을 경우
  {
      Serial.println("push");
      myservo.write(0);
    
     if(mark==0)
     { 
         cardid="1";
         mark=1;
     }
     else
     {
         cardid="1";
         mark=0;
         //check=0;
      }
  }
}

void UART_Send_Byte(unsigned char command_data)
{//send byte to device
  Serial.write(command_data);
  Serial.flush();// complete the transmission of outgoing serial data
}

void wake_card(void)
{//send wake[] to device
  unsigned char i;
  for(i=0;i<24;i++) //send command
    UART_Send_Byte(wake[i]);
}

void firmware_version(void)
{//send firmware[] to device
  unsigned char i;
  for(i=0;i<9;i++) //send command
    UART_Send_Byte(firmware[i]);
}

void send_tag(void)
{//send tag[] to device
  unsigned char i;
  for(i=0;i<11;i++) //send command
    UART_Send_Byte(tag[i]);
}

void read_ACK(unsigned char temp)
{//read ACK into reveive_ACK[]
  //Serial.println("read the result");
  unsigned char i;
  for(i=0;i<temp;i++) {
    receive_ACK[i]= Serial.read();
  }
}

void display(unsigned char tem)
{
  for (int i=0;i<tem;i++)
  {
    if (receive_ACK[i] < 16) // to make 2 digits
      Serial.print("0");
    Serial.print(receive_ACK[i], HEX);
    if (i<(tem-1)) Serial.print(" ");
  }
  Serial.println();
}

void copy_id (void)
{//save old id
  int i;
  for (i=0 ; i<5 ; i++) {
    old_id[i] = cur_id[i];
  }
}

// read tag id from tag values
void read_id (void) {
  int ai, ci;
  for (ci=0, ai=19; ci<5; ci++,ai++) {
    cur_id[ci] = receive_ACK[ai];
  }
}

//return true if find id is old
char same_id (void)
{
  int ai, oi;
  for (oi=0,ai=19; oi<5; oi++,ai++) {
    if (old_id[oi] != receive_ACK[ai])
      return 0;
  }
  return 1;
}

// true if tag is FF FF FF FF FF
char no_detection(void) {
  int i;
  for (i=0 ; i<sizeof(cur_id) ; i++) {
    if (cur_id[i] != 255)  // FF
      return 0;
  }
  return 1;
}

void print_id() {
  for (int i=0 ; i<sizeof(cur_id) ; i++) {
    if(i == 0){
      cardid = "";
    }
    if (cur_id[i] < 16) // to make 2 digits
    {
      Serial.print("0");
      BTSerial.print("0");
   }
    Serial.print(cur_id[i], HEX);
    BTSerial.print(cur_id[i], HEX);
  
    if (i<(sizeof(cur_id)-1)) 
    {
      Serial.print(" ");
      BTSerial.print(" ");
    }
     cardid += cur_id[i];
    if(i<6)
    {
//    cardid += cur_id[i];
    }
  }

  Serial.println();
  BTSerial.println();

}

void who_family()
{
   BTSerial.println(family);
  }