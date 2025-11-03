# Zadanie 2

## 1)

Nie jest to poprawne działanie.

Np.

| Czas / Krok | Proces P | Proces K | dzialaj |
|-------------|----------|----------|---------|
| T1          | P1       | K1       | true    |
| T2          | P1       | K2       | true    |
| T3          | P1       | K3       | true    |
| T4          | P2       | K3       | false    |
| **T5**      | **P3**   | **K3**   | **false** |

czyli dochodzi do wzajemnego wykluczania
## 2)

Nie jest to poprawne działanie.

Np.

| Czas / Krok | Proces P | Proces K | dzialaj |
|-------------|----------|----------|---------|
| T1          | P1       | K1       | true    |
| T2          | P2       | K1       | true    |
| T3          | P2       | K2       | true    |
| T4          | P3       | K2       | false   |
| T5          | P3       | K3       | false   |
| **T6**      | **P4**   | **K3**   | **false** |

## 3)

Jest to poprawne działanie. Chyba że jeden z procesów przestanie działać, wtedy drugi będzie deadlocked.

## 4)

Nie poprawne

| Czas / Krok | Proces P | Proces K | turaP    | turaK    |
|-------------|----------|----------|----------|----------|
| T1          | P1       | K1       | false    | false    |
| T2          | P2       | K1       | false    | false    |
| T3          | P2       | K2       | false    | false    |
| T4          | P3       | K2       | true     | false    |
| T5          | P3       | K3       | true     | true     |
| T6          | P4       | K3       | true     | true     |
| **T7**      | **P4**   | **K4**   | **true** | **true** |


## 5)

Nie poprawne

| Czas / Krok | Proces P | Proces K | turaP    | turaK    |
|-------------|----------|----------|----------|----------|
| T1          | P1       | K1       | false    | false    |
| T2          | P2       | K1       | true     | false    |
| T3          | P2       | K2       | true     | true     |
| T4          | P3       | K2       | true     | true     |
| **T5**      | **P3**   | **K3**   | **true** | **true** |

Dochodzi do deadlocku

## 6)

Nie jest zapewniony brak zagłodzenia. Istnieje scenariusz gdzie np. *Proces K* ma ciągły dostęp do sekcji krytycznej (przez przykładowo krótką sekcje lokalną (K1) i przedłużenie w pętli w procesie P (P4 i P5))

| Czas / Krok | Proces P | Proces K | turaP | turaK  |
|-------------|----------|----------|-------|--------|
| T1          | P1       | K1       | false | false  |
| T2          | P2       | K1       | true  | false  |
| T3          | P3       | K1       | true  | false  |
| T4          | P6       | K1       | true  | false  |
| T3          | P7       | K1       | false | false  |
| T3          | P1       | K1       | false | false  |
| T3          | P2       | K1       | true  | false  |
| T3          | P3       | K1       | true  | false  |
| T3          | P6       | K1       | true  | false  |