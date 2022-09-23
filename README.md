# Repositório para os trabalhos T1 e T2 da cadeira de Teoria da Informação

## Benchmark de compressão das codificações

- alice29 = 152089 bytes
- sum = 38240 bytes

### Codificação utilizando histograma+dicionário

| Encoding     | alice29 (encoded)       | alice29 (encrypted)   |   | sum (encoded)         | sum (encrypted)        |
|--------------|-------------------------|-----------------------|---|-----------------------|------------------------|
| GOLOMB(K=2)  | 115958 bytes (76,2%)    | 202870 bytes (133,4%) |   | 65928 bytes (172,4%)  | 115182 bytes (301,2%)  |
| GOLOMB(K=4)  | 92263 bytes (60,7%)     | 161404 bytes (106,1%) |   | 41846 bytes (109,4%)  | 73038 bytes (191,0%)   |
| GOLOMB(K=8)  | 90342 bytes (59,4%)     | 158042 bytes (103,9%) |   | 32228 bytes (84,3%)   | 56207 bytes (147,0%)   |
| GOLOMB(K=16) | 99541 bytes (65,4%)     | 174140 bytes (114,5%) |   | 29875 bytes (78,1%)   | 52089 bytes (136,2%)   |
| GOLOMB(K=32) | 114782 bytes (75,5%)    | 200812 bytes (132,0%) |   | 31172 bytes (81,5%)   | 54359 bytes (142,2%)   |
| GOLOMB(K=64) | 133164 bytes (87,6%)    | 232981 bytes (153,2%) |   | 34438 bytes (90,1%)   | 60074 bytes (157,1%)   |
| ELIAS_GAMMA  | 101405 bytes (66,7%)    | 177402 bytes (116,6%) |   | 30592 bytes (80,0%)   | 53344 bytes (139,5%)   |
| FIBONACCI    | 95669 bytes (62,9%)     | 167364 bytes (110,0%) |   | 27838 bytes (72,8%)   | 48524 bytes (126,9%)   |
| UNARY        | 182953 bytes (120,3%)   | 320111 bytes (210,5%) |   | 118928 bytes (311,0%) | 207932 bytes (543,8%)  |
| DELTA        | 164014 bytes (107,8%)   | 286968 bytes (188,7%) |   | 37997 bytes (99,4%)   | 66303 bytes (173,4%)   |

### Codificação sem utilizar histograma+dicionário

| Encoding     | alice29 (encoded)       | alice29 (encrypted)     |   | sum (encoded)         | sum (encrypted)        |
|--------------|-------------------------|-------------------------|---|-----------------------|------------------------|
| GOLOMB(K=2)  | 838632 bytes (551,4%)   | 1467606 bytes (965,0%)  |   | 141296 bytes (369,5%) | 247268 bytes (646,6%)  |
| GOLOMB(K=4)  | 454368 bytes (298,8%)   | 795144 bytes (522,8%)   |   | 79503 bytes (207,9%)  | 139130 bytes (363,8%)  |
| GOLOMB(K=8)  | 270194 bytes (177,7%)   | 472839 bytes (310,9%)   |   | 50992 bytes (133,3%)  | 89236 bytes (233,4%)   |
| GOLOMB(K=16) | 188835 bytes (124,2%)   | 330461 bytes (217,3%)   |   | 39254 bytes (102,7%)  | 68694 bytes (179,6%)   |
| GOLOMB(K=32) | 158804 bytes (104,4%)   | 277907 bytes (182,7%)   |   | 35528 bytes (92,9%)   | 62174 bytes (162,6%)   |
| GOLOMB(K=64) | 146678 bytes (96,4%)    | 256686 bytes (168,8%)   |   | 35755 bytes (93,5%)   | 62571 bytes (163,6%)   |
| ELIAS_GAMMA  | 232763 bytes (153,0%)   | 407335 bytes (267,8%)   |   | 39424 bytes (103,1%)  | 68992 bytes (180,4%)   |
| FIBONACCI    | 191955 bytes (126,2%)   | 335921 bytes (220,9%)   |   | 34685 bytes (90,7%)   | 60699 bytes (158,7%)   |
| UNARY        | 1628760 bytes (1070,9%) | 2850330 bytes (1874,1%) |   | 269714 bytes (705,3%) | 471999 bytes (1234,3%) |
| DELTA        | 163939 bytes (107,8%)   | 286893 bytes (188,6%)   |   | 37741 bytes (98,7%)   | 66047 bytes (172,7%)   |
