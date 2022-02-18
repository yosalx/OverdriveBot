# Tugas Besar 1 Strategi Algoritma 

### Pemanfaatan Algoritma Greedy Dalam Aplikasi Permainan “Overdrive”

##### Safiq Faray / 13519145
##### Kevin Roni / 13520114
##### Yoseph A.S. / 13520141

## Deskripsi Program
Program ini mengimplementasikan Greedy by Speed dalam pembuatan bot nya. Greedy by speed adalah mengurutkan command berdasarkan kecepatan yang mungkin untuk round berikutnya. Alternatif ini akan menganalisis situasi bot pada saat tersebut, lalu menganalisis command yang dapat memberikan kecepatan paling maksimal bagi bot untuk round berikutnya. Idenya adalah dengan mengoptimasi kecepatan tiap ronde, akan mencapai garis finish dengan waktu secepat mungkin.

## Requirements

#### Java (minimal Java 8)
#### Apache Maven / IntelIiJ IDEA

## Directory

```sh
├── src                     # Berisi source code program
├── bin                     # Berisi executable
├── doc                     # Berisi laporan dari Tugas Kecil 1 ini
```

## How to Run
#### 1. Download starter-pack : https://github.com/EntelectChallenge/2020-Overdrive/releases/tag/2020.3.4
#### 2. Clone repo kemudian replace folder java pada starter-bots dengan folder src pada repo ini
#### 3. Build starter-bot dengan maven dan ganti file.jar with dependencies pada folder target yang terbentuk dengan file .jar pada folder bin di repo ini
#### 4. Ubah konfigurasi bot.json menjadi "botFileName": Hop On Valorant.jar",

#### Ubah game-runner-config.json di folder starter-pack menjadi
  "player-a": "./starter-bots/java",
  "player-b": "./reference-bot/java",
#### Jalankan file run.bat
