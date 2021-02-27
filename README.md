# custom-calendar

version-1.0.0 [Google play store](https://play.google.com/store/apps/details?id=sej.calendar.customcalendar)

한 달 당 일수(N)를 바꿀 수 있습니다. 
You can change the day per month. 
![KakaoTalk_20210131_203718971](https://user-images.githubusercontent.com/63631604/107145399-89986e80-6984-11eb-9fd9-16f030e8fe85.jpg)

28일 달력의 모습
This is 28 day calendar.   
![KakaoTalk_20210131_203606277_02](https://user-images.githubusercontent.com/63631604/107144252-5ef6e780-697d-11eb-884b-5915afab091e.jpg)

40일 달력의 모습
This is 40 day calendar.    
![KakaoTalk_20210131_203756450](https://user-images.githubusercontent.com/63631604/107144256-61594180-697d-11eb-9316-7634a29fbfa2.jpg)

메모를 추가, 편집, 삭제할 수 있습니다. 
You can add and edit and delete the memo.  
![KakaoTalk_20210131_203606277_01](https://user-images.githubusercontent.com/63631604/107144249-5acaca00-697d-11eb-82a2-5e348d94191d.jpg)

### 참고 

* N 변경 시 메모의 위치는 그레고리 달력을 기준으로 변경됩니다. When N changes, the position of the note changes based on the Gregorian Calendar. 
* 마지막 달의 일수는 N보다 적거나 큽니다. 365(혹은 366)을 N으로 나눈 나머지가 N의 0.3 배 이상일 경우, 마지막 달의 일수는 나머지와 같으며,  0.3 배 이하일 경우, 마지막달은 나머지 + N입니다.    
```ex) N = 28일 때 , 365 % 28 = 1 <= 0.3 * 28 이므로 마지막 달은 28+1일입니다.```   
The days of the last month is less than N or greater than N. If the remainder that divide 365(or 366) into N is more than 0.3 times of N, the days of the last month is equal to the remainder , if it is less than 0.3 times, the days of the last month is equal to remainder + N.  
```ex) If N = 28, 365% 28 = 1 <= 0.3 * 28, so the last month is 28+1 days```
