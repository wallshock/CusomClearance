
# O środowisku
Zadanie zostało zrealizowane w Scali 3.3.1

# Wymagania

- Scala 3.3.2
- openjdk-21

# Uruchamianie

- Projekt uruchamiamy za pomocą klasy Simulation i operujemy z konsoli

# Opis rozwiązania

Założenia:
- Ciężarówki przyjeżdzają w równych odstepach czasowych. Zatem przetwarzamy wszystko sekwencyjnie i z założenia do DocumentCheck przyjeżdża jedna ciężarówka naraz (choć jak damy arrive(int) kilkukrotnie to system dalej będzie działał)
- Algorytm: Możemy zamieniać się miejscami w kolejce, nawet gdy jedno miejsce jest puste. Miejsca nie są sztywno ustalone mają charakter kolejki.
  ![Kolejka](algorytm.png)

System:

- truckList: Jest to kolejka priorytetowa ciężarówek, które czekają na odprawę celną.

- queues: Lista kolejek, do których ciężarówki są kierowane po przeprowadzeniu kontroli dokumentów.

- documentControlGate i goodsControlGates: To są bramki, przez które ciężarówki przechodzą podczas kontroli dokumentów i towarów.

- truckManager i queueManager: klasy odpowiedzialne za zarządzanie ciężarówkami i kolejkami.

- handleState: Głowna metoda odpowiedzialna za obsługę różnych stanów, w których może znajdować się ciężarówka w naszym systemie (np. przybycie, kontrola dokumentów, oczekiwanie w kolejce, kontrola towarów, wyjazd).

- do API nalezą: arrive, step, status, waitingTime

Rozwiązanie polega na tym, że mamy liste niezmiennych stanów w których mogą się znajdować nasze ciężarówki a my jedynie je processujemy. Głowny koncept polega na tym że kontrolujemy flow w systemie za pomocą uporządkowywania ciężarówek w zaleznosci od stanu.



Algorytm:
  - Zmiana elementów między sobą przynosi korzyść tylko przy kolejkach różnych długości (inaczej zysk jednego jest stratą drugiego)
  - Potrzebujemy rozpatrywać kolejki dłuższe oraz krótsze
  - Potrzebujemy rozpatrywać kolejki o niższm oraz wyższym koszcie początkowym (koszt poczatkowy = cieżarowka 1 w kolejce + cieżarówka w bramie) jako, że nie możemy zmienic tych wartosci musimy się do nich dostosowywać
  - W zależności od sytuacji stosujemy odpowiednią heurystykę (Implementacja w QueueManager)


Dodatkowe informacje:
- Nie patrzyłem na to aby printowanie było umieszczone konsekwentnie w kodzie, jest jedynie pomocą dla zobaczenia zmiany stanów.
- Konsekwentne printowanie jest zrobione dla kazdej lokacji gdy do niej wchodzimy lub ja opuszczmy, jednak syfi konsole można to wyłączyc w TruckManager.

# Co można było zrobić lepiej

- Utworzenie util/TruckPriorityQueue nie było konieczne, wynikało z tego jak przetwarzamy stany pojazdów w naszej aplikacji, przetwarzanie stanów zdecydowanie da się zrobić w bardziej optymalny sposób 

- Mając na uwadze modularną strukturę i skalowalność aplikacji, algorytm powinien również posiadać tę cechę. Obecne rozwiązanie, obejmujące jedynie obsługę dwóch kolejek, nie spełnia tego założenia.