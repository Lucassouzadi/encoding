
# Repositório para os trabalhos T1 e T2 da cadeira de Teoria da Informação
### Objetivo do T1:
Elaborar  uma  solução  computacional  que  codifique  (compacte)  e  decodifique (descompacte) arquivos. Para isto deve ser implementado um protótipo (usando a linguagem de sua escolha) que deve ser testado com a compactação e descompactação dos  arquivos  alice29.txt e sum, do corpus de  Canterbury (corpus.canterbury.ac.nz/descriptions/#cantrbry).
A meta é desenvolver uma solução de compactação (sem perda –  lossless) empregando as abordagens de codificação a nível de  símbolo. As formas de codificação que devem ser suportadas são: Golomb, Elias-Gamma, Fibonacci, Unária e Delta. O usuário deve poder escolher o tipo de compactação que será empregado, bem como o arquivo a ser codificado/decodificado.
### Objetivo do T2:
Neste trabalho deve ser acrescentado ao cenário do T1 técnicas de  tratamento de ruído (códigos de correção de erro - ECC). Essa funcionalidade pode ser acrescentada de maneira separada/independente ou acrescida/integrada às implementações do encoder e do decoder.
Depois do arquivo ter sido codificado, ele é recodificado ganhando informação adicional para tratamento de ruído. Devem ser implementadas duas técnicas: 
* Logo após o cabeçalho do arquivo, deve ser acrescentado/gravado um byte resultante do cálculo CRC-8 (ATM) dos dois bytes do cabeçalho;
* Depois disso serão armazenados os  codewords  Hamming formados a partir da leitura da informação dos  codewords  presentes no arquivo codificado. Por ex.: a cada 4 bits dos  codewords  do arquivo codificado alice29.cod é gerado um codeword  Hamming de 7 bits que será armazenado no arquivo alice29.ecc
## Modo de usar
Na raíz do projeto está o executável `encoding.jar` com o build do projeto. A partir dele é possível rodar o programa passando argumentos no formato `chave:valor` para parametrizá-lo (apesar de todos os parâmetros terem valores padrão, fazendo com que nenhum seja obrigatório).
### Exemplo de execução: 
```bash
$ java -jar encoding.jar action:encodeAndDecode file:alice29.txt encoding:0 arg:8 useDictionary:true logHistogram:false

---- Parâmetros aplicados: ----
        action:encodeAndDecode
        file:alice29.txt
        encoding:0
        arg:8
        useDictionary:true
        logHistogram:false
-------------------------------

C:\git\alice29.txt encoded to C:\git\alice29.cod using GOLOMB(K=8)
C:\git\alice29.cod encrypted to C:\git\alice29.ecc using CRC8 for header and Hamming(7,4) for data
152089 bytes --> 90342 bytes --> 158042 bytes
C:\git\alice29.ecc decrypted to C:\git\alice29.dcc validating CRC8 on header and Hamming(7,4) for data
C:\git\alice29.dcc decoded to C:\git\alice29.dec using GOLOMB(K=8)
158042 bytes --> 90342 bytes --> 152089 bytes

```
Argumentos recebidos por linha de comando:

action
~ Tipo: String
Valor padrão: ´encodeAndDecode´
Descrição: Define qual fluxo será executado, tem as seguintes opções:
* encode: realiza a codificação e encriptação do arquivo especificado para arquivos `.cod` e `.ecc`, respectivamente;
* decode: realiza a decodificação e decriptação do arquivo especificado para arquivos `.dcc` e `.dec`, respectivamente;
* encodeAndDecode: realiza ambas, gera os arquivos `.cod` --> `.ecc` --> `.dcc` --> `.dec`;
* testHamming: roda a bateria de testes em cima da implementação do `Hamming(7,4)`, aplicando ruído em todos os bits das codewords de 0000 a 1111;
* testCRC: roda testes em cima da implementação do `CRC8` calculando o valor do crc para algumas seguências de bytes.
<br>

file
~ Tipo: String
Valor padrão: `alice29.txt`
Descrição: 
<br>

encoding
~ Tipo: int
Valor padrão: `0`
Descrição: Identificador que determinará qual a codificação que será usada para codificar e/ou decodificar o arquivo:
* 0: Golomb
* 1: Elias-Gamma
* 2: Fibonacci
* 3: Unário
* 4: Delta
<br>

arg
~ Tipo: int
Valor padrão: `8`
Descrição: Parâmetro da codificação.
* Valor do divisor, caso a codificação seja Golomb (0);
* Stop bit (0 ou 1), caso a codificação seja Unária (3);
* Para qualquer outra, este parâmetro é desconsiderado
<br>

useDictionary
~ Tipo: boolean
Valor padrão: `true`
Descrição: Define se será utilizado dicionário ao codificar/decodificar o arquivo.
Caso habilitado, será feita uma pré-análise do arquivo para se montar o histograma dos bytes e, com base nele, é criado um dicionário de tamanho N que mapeará os bytes de acordo com a frequência, colocando os que estão no topo do histograma com valores menores. O dicionário é salvo no cabeçalho do arquivo logo após os dois primeiros bytes da codificação, no seguinte formato: {1 byte com o valor de N - 1} {N bytes contendo as codewords ordenadas por ordem de frequência, de forma que as primeiras serão mapeadas para os menores valores}
<br>

logHistogram
~ Tipo: boolean
Valor padrão: `false`
Descrição: Define se o histograma e o dicionário resultante serão impressos, no seguinte formato:
<details>
  <summary>
    Dicionário criado para a codificação do arquivo alice29.txt:
  </summary>
  <pre>
Histograma completo:
	[ ] 0x00 (0): 0 ocorrências
	[] 0x01 (1): 0 ocorrências
	[] 0x02 (2): 0 ocorrências
	[] 0x03 (3): 0 ocorrências
	[] 0x04 (4): 0 ocorrências
	[] 0x05 (5): 0 ocorrências
	[] 0x06 (6): 0 ocorrências
	[] 0x07 (7): 0 ocorrências
	] 0x08 (8): 0 ocorrências
	[	] 0x09 (9): 0 ocorrências
	[
] 0x0A (10): 3608 ocorrências
	[] 0x0B (11): 0 ocorrências
	[] 0x0C (12): 0 ocorrências
] 0x0D (13): 3608 ocorrências
	[] 0x0E (14): 0 ocorrências
	[] 0x0F (15): 0 ocorrências
	[] 0x10 (16): 0 ocorrências
	[] 0x11 (17): 0 ocorrências
	[] 0x12 (18): 0 ocorrências
	[] 0x13 (19): 0 ocorrências
	[] 0x14 (20): 0 ocorrências
	[] 0x15 (21): 0 ocorrências
	[] 0x16 (22): 0 ocorrências
	[] 0x17 (23): 0 ocorrências
	[] 0x18 (24): 0 ocorrências
	[] 0x19 (25): 0 ocorrências
	[] 0x1A (26): 1 ocorrências
	[] 0x1B (27): 0 ocorrências
	[] 0x1C (28): 0 ocorrências
	[] 0x1D (29): 0 ocorrências
	[] 0x1E (30): 0 ocorrências
	[] 0x1F (31): 0 ocorrências
	[ ] 0x20 (32): 28900 ocorrências
	[!] 0x21 (33): 449 ocorrências
	["] 0x22 (34): 113 ocorrências
	[#] 0x23 (35): 0 ocorrências
	[$] 0x24 (36): 0 ocorrências
	[%] 0x25 (37): 0 ocorrências
	[&] 0x26 (38): 0 ocorrências
	['] 0x27 (39): 1761 ocorrências
	[(] 0x28 (40): 56 ocorrências
	[)] 0x29 (41): 55 ocorrências
	[*] 0x2A (42): 60 ocorrências
	[+] 0x2B (43): 0 ocorrências
	[,] 0x2C (44): 2418 ocorrências
	[-] 0x2D (45): 669 ocorrências
	[.] 0x2E (46): 977 ocorrências
	[/] 0x2F (47): 0 ocorrências
	[0] 0x30 (48): 0 ocorrências
	[1] 0x31 (49): 0 ocorrências
	[2] 0x32 (50): 1 ocorrências
	[3] 0x33 (51): 0 ocorrências
	[4] 0x34 (52): 0 ocorrências
	[5] 0x35 (53): 0 ocorrências
	[6] 0x36 (54): 0 ocorrências
	[7] 0x37 (55): 0 ocorrências
	[8] 0x38 (56): 0 ocorrências
	[9] 0x39 (57): 1 ocorrências
	[:] 0x3A (58): 233 ocorrências
	[;] 0x3B (59): 194 ocorrências
	[<] 0x3C (60): 0 ocorrências
	[=] 0x3D (61): 0 ocorrências
	[>] 0x3E (62): 0 ocorrências
	[?] 0x3F (63): 202 ocorrências
	[@] 0x40 (64): 0 ocorrências
	[A] 0x41 (65): 638 ocorrências
	[B] 0x42 (66): 91 ocorrências
	[C] 0x43 (67): 144 ocorrências
	[D] 0x44 (68): 192 ocorrências
	[E] 0x45 (69): 188 ocorrências
	[F] 0x46 (70): 74 ocorrências
	[G] 0x47 (71): 82 ocorrências
	[H] 0x48 (72): 284 ocorrências
	[I] 0x49 (73): 733 ocorrências
	[J] 0x4A (74): 8 ocorrências
	[K] 0x4B (75): 82 ocorrências
	[L] 0x4C (76): 98 ocorrências
	[M] 0x4D (77): 200 ocorrências
	[N] 0x4E (78): 120 ocorrências
	[O] 0x4F (79): 176 ocorrências
	[P] 0x50 (80): 64 ocorrências
	[Q] 0x51 (81): 84 ocorrências
	[R] 0x52 (82): 140 ocorrências
	[S] 0x53 (83): 218 ocorrências
	[T] 0x54 (84): 472 ocorrências
	[U] 0x55 (85): 66 ocorrências
	[V] 0x56 (86): 42 ocorrências
	[W] 0x57 (87): 237 ocorrências
	[X] 0x58 (88): 4 ocorrências
	[Y] 0x59 (89): 114 ocorrências
	[Z] 0x5A (90): 1 ocorrências
	[[] 0x5B (91): 2 ocorrências
	[\] 0x5C (92): 0 ocorrências
	[]] 0x5D (93): 2 ocorrências
	[^] 0x5E (94): 0 ocorrências
	[_] 0x5F (95): 4 ocorrências
	[`] 0x60 (96): 1108 ocorrências
	[a] 0x61 (97): 8149 ocorrências
	[b] 0x62 (98): 1383 ocorrências
	[c] 0x63 (99): 2253 ocorrências
	[d] 0x64 (100): 4739 ocorrências
	[e] 0x65 (101): 13381 ocorrências
	[f] 0x66 (102): 1926 ocorrências
	[g] 0x67 (103): 2446 ocorrências
	[h] 0x68 (104): 7088 ocorrências
	[i] 0x69 (105): 6778 ocorrências
	[j] 0x6A (106): 138 ocorrências
	[k] 0x6B (107): 1076 ocorrências
	[l] 0x6C (108): 4615 ocorrências
	[m] 0x6D (109): 1907 ocorrências
	[n] 0x6E (110): 6893 ocorrências
	[o] 0x6F (111): 7965 ocorrências
	[p] 0x70 (112): 1458 ocorrências
	[q] 0x71 (113): 125 ocorrências
	[r] 0x72 (114): 5293 ocorrências
	[s] 0x73 (115): 6277 ocorrências
	[t] 0x74 (116): 10212 ocorrências
	[u] 0x75 (117): 3402 ocorrências
	[v] 0x76 (118): 803 ocorrências
	[w] 0x77 (119): 2437 ocorrências
	[x] 0x78 (120): 144 ocorrências
	[y] 0x79 (121): 2150 ocorrências
	[z] 0x7A (122): 77 ocorrências
	[{] 0x7B (123): 0 ocorrências
	[|] 0x7C (124): 0 ocorrências
	[}] 0x7D (125): 0 ocorrências
	[~] 0x7E (126): 0 ocorrências
	[] 0x7F (127): 0 ocorrências
	[] 0x80 (128): 0 ocorrências
	[] 0x81 (129): 0 ocorrências
	[] 0x82 (130): 0 ocorrências
	[] 0x83 (131): 0 ocorrências
	[] 0x84 (132): 0 ocorrências
	[] 0x85 (133): 0 ocorrências
	[] 0x86 (134): 0 ocorrências
	[] 0x87 (135): 0 ocorrências
	[] 0x88 (136): 0 ocorrências
	[] 0x89 (137): 0 ocorrências
	[] 0x8A (138): 0 ocorrências
	[] 0x8B (139): 0 ocorrências
	[] 0x8C (140): 0 ocorrências
	[] 0x8D (141): 0 ocorrências
	[] 0x8E (142): 0 ocorrências
	[] 0x8F (143): 0 ocorrências
	[] 0x90 (144): 0 ocorrências
	[] 0x91 (145): 0 ocorrências
	[] 0x92 (146): 0 ocorrências
	[] 0x93 (147): 0 ocorrências
	[] 0x94 (148): 0 ocorrências
	[] 0x95 (149): 0 ocorrências
	[] 0x96 (150): 0 ocorrências
	[] 0x97 (151): 0 ocorrências
	[] 0x98 (152): 0 ocorrências
	[] 0x99 (153): 0 ocorrências
	[] 0x9A (154): 0 ocorrências
	[] 0x9B (155): 0 ocorrências
	[] 0x9C (156): 0 ocorrências
	[] 0x9D (157): 0 ocorrências
	[] 0x9E (158): 0 ocorrências
	[] 0x9F (159): 0 ocorrências
	[ ] 0xA0 (160): 0 ocorrências
	[¡] 0xA1 (161): 0 ocorrências
	[¢] 0xA2 (162): 0 ocorrências
	[£] 0xA3 (163): 0 ocorrências
	[¤] 0xA4 (164): 0 ocorrências
	[¥] 0xA5 (165): 0 ocorrências
	[¦] 0xA6 (166): 0 ocorrências
	[§] 0xA7 (167): 0 ocorrências
	[¨] 0xA8 (168): 0 ocorrências
	[©] 0xA9 (169): 0 ocorrências
	[ª] 0xAA (170): 0 ocorrências
	[«] 0xAB (171): 0 ocorrências
	[¬] 0xAC (172): 0 ocorrências
	[­] 0xAD (173): 0 ocorrências
	[®] 0xAE (174): 0 ocorrências
	[¯] 0xAF (175): 0 ocorrências
	[°] 0xB0 (176): 0 ocorrências
	[±] 0xB1 (177): 0 ocorrências
	[²] 0xB2 (178): 0 ocorrências
	[³] 0xB3 (179): 0 ocorrências
	[´] 0xB4 (180): 0 ocorrências
	[µ] 0xB5 (181): 0 ocorrências
	[¶] 0xB6 (182): 0 ocorrências
	[·] 0xB7 (183): 0 ocorrências
	[¸] 0xB8 (184): 0 ocorrências
	[¹] 0xB9 (185): 0 ocorrências
	[º] 0xBA (186): 0 ocorrências
	[»] 0xBB (187): 0 ocorrências
	[¼] 0xBC (188): 0 ocorrências
	[½] 0xBD (189): 0 ocorrências
	[¾] 0xBE (190): 0 ocorrências
	[¿] 0xBF (191): 0 ocorrências
	[À] 0xC0 (192): 0 ocorrências
	[Á] 0xC1 (193): 0 ocorrências
	[Â] 0xC2 (194): 0 ocorrências
	[Ã] 0xC3 (195): 0 ocorrências
	[Ä] 0xC4 (196): 0 ocorrências
	[Å] 0xC5 (197): 0 ocorrências
	[Æ] 0xC6 (198): 0 ocorrências
	[Ç] 0xC7 (199): 0 ocorrências
	[È] 0xC8 (200): 0 ocorrências
	[É] 0xC9 (201): 0 ocorrências
	[Ê] 0xCA (202): 0 ocorrências
	[Ë] 0xCB (203): 0 ocorrências
	[Ì] 0xCC (204): 0 ocorrências
	[Í] 0xCD (205): 0 ocorrências
	[Î] 0xCE (206): 0 ocorrências
	[Ï] 0xCF (207): 0 ocorrências
	[Ð] 0xD0 (208): 0 ocorrências
	[Ñ] 0xD1 (209): 0 ocorrências
	[Ò] 0xD2 (210): 0 ocorrências
	[Ó] 0xD3 (211): 0 ocorrências
	[Ô] 0xD4 (212): 0 ocorrências
	[Õ] 0xD5 (213): 0 ocorrências
	[Ö] 0xD6 (214): 0 ocorrências
	[×] 0xD7 (215): 0 ocorrências
	[Ø] 0xD8 (216): 0 ocorrências
	[Ù] 0xD9 (217): 0 ocorrências
	[Ú] 0xDA (218): 0 ocorrências
	[Û] 0xDB (219): 0 ocorrências
	[Ü] 0xDC (220): 0 ocorrências
	[Ý] 0xDD (221): 0 ocorrências
	[Þ] 0xDE (222): 0 ocorrências
	[ß] 0xDF (223): 0 ocorrências
	[à] 0xE0 (224): 0 ocorrências
	[á] 0xE1 (225): 0 ocorrências
	[â] 0xE2 (226): 0 ocorrências
	[ã] 0xE3 (227): 0 ocorrências
	[ä] 0xE4 (228): 0 ocorrências
	[å] 0xE5 (229): 0 ocorrências
	[æ] 0xE6 (230): 0 ocorrências
	[ç] 0xE7 (231): 0 ocorrências
	[è] 0xE8 (232): 0 ocorrências
	[é] 0xE9 (233): 0 ocorrências
	[ê] 0xEA (234): 0 ocorrências
	[ë] 0xEB (235): 0 ocorrências
	[ì] 0xEC (236): 0 ocorrências
	[í] 0xED (237): 0 ocorrências
	[î] 0xEE (238): 0 ocorrências
	[ï] 0xEF (239): 0 ocorrências
	[ð] 0xF0 (240): 0 ocorrências
	[ñ] 0xF1 (241): 0 ocorrências
	[ò] 0xF2 (242): 0 ocorrências
	[ó] 0xF3 (243): 0 ocorrências
	[ô] 0xF4 (244): 0 ocorrências
	[õ] 0xF5 (245): 0 ocorrências
	[ö] 0xF6 (246): 0 ocorrências
	[÷] 0xF7 (247): 0 ocorrências
	[ø] 0xF8 (248): 0 ocorrências
	[ù] 0xF9 (249): 0 ocorrências
	[ú] 0xFA (250): 0 ocorrências
	[û] 0xFB (251): 0 ocorrências
	[ü] 0xFC (252): 0 ocorrências
	[ý] 0xFD (253): 0 ocorrências
	[þ] 0xFE (254): 0 ocorrências
	[ÿ] 0xFF (255): 0 ocorrências

Dicionário resultante(tamanho=74):
	[ ] 0x20, com 28900 ocorrências, mapeado para 0x00
	[e] 0x65, com 13381 ocorrências, mapeado para 0x01
	[t] 0x74, com 10212 ocorrências, mapeado para 0x02
	[a] 0x61, com 8149 ocorrências, mapeado para 0x03
	[o] 0x6F, com 7965 ocorrências, mapeado para 0x04
	[h] 0x68, com 7088 ocorrências, mapeado para 0x05
	[n] 0x6E, com 6893 ocorrências, mapeado para 0x06
	[i] 0x69, com 6778 ocorrências, mapeado para 0x07
	[s] 0x73, com 6277 ocorrências, mapeado para 0x08
	[r] 0x72, com 5293 ocorrências, mapeado para 0x09
	[d] 0x64, com 4739 ocorrências, mapeado para 0x0A
	[l] 0x6C, com 4615 ocorrências, mapeado para 0x0B
] 0x0D, com 3608 ocorrências, mapeado para 0x0C
	[
] 0x0A, com 3608 ocorrências, mapeado para 0x0D
	[u] 0x75, com 3402 ocorrências, mapeado para 0x0E
	[g] 0x67, com 2446 ocorrências, mapeado para 0x0F
	[w] 0x77, com 2437 ocorrências, mapeado para 0x10
	[,] 0x2C, com 2418 ocorrências, mapeado para 0x11
	[c] 0x63, com 2253 ocorrências, mapeado para 0x12
	[y] 0x79, com 2150 ocorrências, mapeado para 0x13
	[f] 0x66, com 1926 ocorrências, mapeado para 0x14
	[m] 0x6D, com 1907 ocorrências, mapeado para 0x15
	['] 0x27, com 1761 ocorrências, mapeado para 0x16
	[p] 0x70, com 1458 ocorrências, mapeado para 0x17
	[b] 0x62, com 1383 ocorrências, mapeado para 0x18
	[`] 0x60, com 1108 ocorrências, mapeado para 0x19
	[k] 0x6B, com 1076 ocorrências, mapeado para 0x1A
	[.] 0x2E, com 977 ocorrências, mapeado para 0x1B
	[v] 0x76, com 803 ocorrências, mapeado para 0x1C
	[I] 0x49, com 733 ocorrências, mapeado para 0x1D
	[-] 0x2D, com 669 ocorrências, mapeado para 0x1E
	[A] 0x41, com 638 ocorrências, mapeado para 0x1F
	[T] 0x54, com 472 ocorrências, mapeado para 0x20
	[!] 0x21, com 449 ocorrências, mapeado para 0x21
	[H] 0x48, com 284 ocorrências, mapeado para 0x22
	[W] 0x57, com 237 ocorrências, mapeado para 0x23
	[:] 0x3A, com 233 ocorrências, mapeado para 0x24
	[S] 0x53, com 218 ocorrências, mapeado para 0x25
	[?] 0x3F, com 202 ocorrências, mapeado para 0x26
	[M] 0x4D, com 200 ocorrências, mapeado para 0x27
	[;] 0x3B, com 194 ocorrências, mapeado para 0x28
	[D] 0x44, com 192 ocorrências, mapeado para 0x29
	[E] 0x45, com 188 ocorrências, mapeado para 0x2A
	[O] 0x4F, com 176 ocorrências, mapeado para 0x2B
	[x] 0x78, com 144 ocorrências, mapeado para 0x2C
	[C] 0x43, com 144 ocorrências, mapeado para 0x2D
	[R] 0x52, com 140 ocorrências, mapeado para 0x2E
	[j] 0x6A, com 138 ocorrências, mapeado para 0x2F
	[q] 0x71, com 125 ocorrências, mapeado para 0x30
	[N] 0x4E, com 120 ocorrências, mapeado para 0x31
	[Y] 0x59, com 114 ocorrências, mapeado para 0x32
	["] 0x22, com 113 ocorrências, mapeado para 0x33
	[L] 0x4C, com 98 ocorrências, mapeado para 0x34
	[B] 0x42, com 91 ocorrências, mapeado para 0x35
	[Q] 0x51, com 84 ocorrências, mapeado para 0x36
	[K] 0x4B, com 82 ocorrências, mapeado para 0x37
	[G] 0x47, com 82 ocorrências, mapeado para 0x38
	[z] 0x7A, com 77 ocorrências, mapeado para 0x39
	[F] 0x46, com 74 ocorrências, mapeado para 0x3A
	[U] 0x55, com 66 ocorrências, mapeado para 0x3B
	[P] 0x50, com 64 ocorrências, mapeado para 0x3C
	[*] 0x2A, com 60 ocorrências, mapeado para 0x3D
	[(] 0x28, com 56 ocorrências, mapeado para 0x3E
	[)] 0x29, com 55 ocorrências, mapeado para 0x3F
	[V] 0x56, com 42 ocorrências, mapeado para 0x40
	[J] 0x4A, com 8 ocorrências, mapeado para 0x41
	[_] 0x5F, com 4 ocorrências, mapeado para 0x42
	[X] 0x58, com 4 ocorrências, mapeado para 0x43
	[]] 0x5D, com 2 ocorrências, mapeado para 0x44
	[[] 0x5B, com 2 ocorrências, mapeado para 0x45
	[Z] 0x5A, com 1 ocorrências, mapeado para 0x46
	[9] 0x39, com 1 ocorrências, mapeado para 0x47
	[2] 0x32, com 1 ocorrências, mapeado para 0x48
	[] 0x1A, com 1 ocorrências, mapeado para 0x49
  </pre>
</details>

## Benchmark de compressão das codificações
- alice29 = 152089 bytes 
  - Dicionário criado com base no histograma: 74 codewords
- sum = 38240 bytes
  - Dicionário criado com base no histograma: 255 codewords

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
