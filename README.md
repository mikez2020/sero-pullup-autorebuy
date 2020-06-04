# sero-pullup-autorebuy

> A slight customization on SERO pullup so as to get share re-buying automated in a secure way.

[SERO pullup](https://github.com/sero-cash/pullup/) is the official decentralized light wallet in which SERO holders can participate the POS process: buy shares, shares voted, shares returned, re-buy shares ... in cycles. And each buying needs to be executed manually.  This program targets to build a mechanism to automate the re-buying to reduce the manual effort, and accordingly, as re-buying can be in time after shares returned, so it's also beneficial for increasing the POS profit.

## Mechanism 

When the pullup program is started, execute the initial share buying by inputting the transaction password and a token. Assume the password is pppp, then the actual input looks like: pppptttt in the password input dialog. Then the program will extract pppp and execute the official share buying logic. At the same time, the program will save the pppp in a secured way by encrypting it then storing it in the memory. 

So next time,  after inputting the token string tttt in the password dialog, the program can recognize it and retrieve password pppp , then execute the official share buying logic.

Based on the above mechanism, a re-buying client program can be built to periodically call APIs to check current SERO balance, and buy shares once balance reaches a threshold.

## Features

- Slight customization on pullup program, almost a totally incremental code plugged into the original code, which makes it easy to upgrade according to the official new pullup release.
- A de-coupled re-buying client program which can be customized by developers. In this repository, just give an example written in Java.
- Token can be customized by the developers for security reason.
- Encryption key for encrypting the password (pppp) is randomly generated, and the random key storing logic can be customized by developers for security reason.

## Build

#### pullup

In [latest released pullup source code](https://github.com/sero-cash/pullup/releases), find light.go, modify according to the indication in the light.go in this repository. 

Indication includes "added part 1", "added part 2", "added part 3" and one "modified part". Current change is based on the source code of "pullup-0.1.16" release version.

Then build pullup.

#### java re-buying client

get the source code and lib, then build it in the way you prefer.

## Usage

```
1, start the customized pullup program
2, execute an intial share buying by inputting pppptttt in the password dialog
3, run java re-buying client in command line. 
```

Four mandatory arguments for the java client :

- from: PK address (not the PKr) which is included in the key file name.
- pool: to which pool you buy the share
- interval: interval for periodically check the SERO balance, in minutes. In testing phase, you can set a short interval, and in practice, several hours could be a balanced value.
- threshold: balance threshold for trigger the re-buying.

## Risks

1, This program is for developers study. Developers should evaluate and do necessary customization by themselves, the author will not afford any responsibilities for any loss caused by using this program.

2, Security is not only about the program, but also highly depends on the machine, OS, network environment.

**Be aware**: this auto re-buying requires the pullup program have to keep running all the time. Strongly recommend to use a dedicated machine with a pure OS to run pullup and whitelist IPs to access the machine.

## License

Licensed under the [GNU General Public License, Version 3](https://github.com/sero-cash/serominer/blob/master/LICENSE).