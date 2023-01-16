# auto-backup
Automatic backup of local files at time intervals.

**일정 시간간격**으로 **로컬 파일/폴더**를 압축하여 백업 폴더에 **저장**하고, 원격 저장소(Google drive)에 **업로드**를 한다.

## Environment
- Java 11
- Google API

## Quick start
1. gradle build
2. java -jar 명령어를 통해 실행
3. 브라우저가 열리면서 원격(Google)에 로그인 요청이 뜸. 수락 및 접근 동의.
4. 실행되던 프로그램(터미널) 잠시 종료. 
5. 최초 실행시 자동으로 config/backup-config.json 파일이 생성된다.
6. backup-config.json을 알맞게 수정한다.
```
{
  "backUpMaxHistory": 10, #로컬/원격에 백업되는 파일 개수. 초과되면 날짜가 빠른 백업파일부터 삭제한다.
  "backUpPeriodMinutes": 10, #백업되는 시간 간격(분), 백업 작업이 끝난 시점으로부터 타이머
  "tempZipFolder": "C:\\dev\\projects\\auto-backup/backup", #백업되는 로컬 폴더
  "backUpZipFileNamePattern": "yyyyMMdd-HH-mm-ss", #백업되는 파일 이름 포멧
  "backUpDirectoryPath": { #백업되는 원격 폴더, paths가 하나의 폴더를 나타낸다.
    "paths": [
      "backup"
    ]
  },
  "sourceFiles": [ #백업 하고자 하는 파일/폴더 목록,'{name, path}'가 하나의 파일/폴더를 의미한다.
    { 
      "name": "pom.xml",
      "path": {
        "prefix": "C:",
        "paths": [
          "Users",
          "user",
          "Desktop",
          "pom"
        ]
      }
    }
  ]
}
```
7. java -jar 명령어를 통해 다시 실행.
8. 백업여부 확인.