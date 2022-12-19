# auto-backup
Automatic backup of local files.

로컬 파일/폴더를 압축하여 백업 폴더에 저장하고, 일정 시간간격으로 원격 저장소(Google drive)에 업로드를 한다.

## 실행 방법
1. java -jar 명령어를 통해 실행(Gradle을 통해 빌드를 했다는 가정하에 서술)
2. 브라우저가 열리면서 원격(Google)에 로그인 요청이 뜸. 수락 및 접근 동의.
3. 실행되던 프로그램(터미널) 잠시 종료. 
4. 최초 실행시 자동으로 config/backup-config.json 파일이 생성된다.
5. backup-config.json을 알맞게 수정한다.
```
{
  "backUpMaxHistory": 10, #백업되는 로컬/원격 폴더의 파일 개수
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
6. java -jar 명령어를 통해 다시 실행.
7. 백업여부 확인.