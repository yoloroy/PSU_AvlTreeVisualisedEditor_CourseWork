**Работа с АВЛ-деревом:**  

![img_ru_03.png](readme%2Fimg_ru_03.png)

Рисунок 3 – Окно работы с АВЛ-Деревом (с открытым меню)

В окне работы с АВЛ-Деревом (Рисунок 3\) нажатие правой кнопки мыши вызывает открытие меню с функциями для взаимодействия с деревом в левом нижнем углу.  
Вызов функций “Вставить”, “Удалить”, “Найти” запускает всплывающее окно “Работа с одним значением” (Рисунок 4). Поле для ввода одного значения обеспечивает выполнение всех трёх функций: Кнопка “+” вставляет значение в дерево, кнопка в виде мусорного бака удаляет значение, также динамически меняется обновляется заголовок поля, показывая, валидно ли значение, есть оно в дереве, нет значения в дереве.

![img_ru_04.png](readme%2Fimg_ru_04.png)

Рисунок 4 – Всплывающее окно “Работа с одним значением”

Функция меню “Модифицировать” открывает окно (Рисунок 5\) с двумя полями: одно для заменяемого значения, другое для заменяющего значения. Оба поля поддерживают проверку валидности значений и проверку, есть ли заменяемое значение в дереве.  

Функция меню “Продемонстрировать проход слева направо”  (Рисунок 6\) при нажатии меняет способ отображения дерева, делая его “плоским”, тем самым демонстрируя проход слева направо.  

![img_ru_05.png](readme%2Fimg_ru_05.png)

Рисунок 5 – Окно функции “Модифицировать”

![img_ru_06.png](readme%2Fimg_ru_06.png)

Рисунок 6 – Отображение дерева при применении функции “Продемонстрировать проход слева направо”

**Работа с Бинарным деревом:**  

![img_ru_07.png](readme%2Fimg_ru_07.png)

Рисунок 7 – Окно работы с Бинарным деревом

В окне работы с Бинарным деревом (Рисунок 7\) нажатие правой кнопки мыши вызывает открытие меню в левом нижнем меню экрана.  

Пункт меню “Продемонстрировать проход слева направо” аналогичен одноимённому пункту из меню в окне для работы с АВЛ-Деревом.  

Пункт меню “Добавить дочерний узел” при нажатии открывает всплывающее окно “Добавить дочерний узел” (Рисунок 8). Меню содержит поля для ввода значений родительского узла (поле сверху) и нового дочернего узла (поле ниже), оба поля поддерживают валидацию и имеет сообщения о том, есть ли родительское значение, возможно ли прикрепить к нему новый узел. В случае, если родительский узел существует и значение для прикрепления к нему валидно, то кнопка “+” активна и при нажатии прикрепляет значение.  

![img_ru_08.png](readme%2Fimg_ru_08.png)

Рисунок 8 – Добавление дочернего узла

Пункт “Найти/удалить узел” открывает окно “Найти/удалить узел”. Единственное поле окна поддерживает валидацию, надпись над полем указывает, есть число или нет, если есть, то кнопка удаления значения становится активной и при нажатии удалит поддерево с указанным значением.  

Пункт “Изменить значение в узле” полностью идентичен одноимённому пункту из меню окна работы с АВЛ-Деревом.  

Пункт “Выполнить поворот вокруг узла” запускает всплывающее окно “Выполнить поворот вокруг узла” (Рисунок 9). В окне есть поле ввода значения, “вокруг” которого должен будет выполниться поворот, значения валидируются и идёт проверка, можно ли повернуть вокруг узла. Кнопки выполнения поворотов становятся активными и не активными в зависимости от того, есть ли необходимые дочерние узлы для осуществления поворота.  

![img_ru_09.png](readme%2Fimg_ru_09.png)

Рисунок 9 – Выполнить поворот вокруг узла

**Сравнение структур:**  

![img_ru_10.png](readme%2Fimg_ru_10.png)

Рисунок 10 – Окно сравнения структур

В окне сравнения структур (Рисунок 10\) есть кнопки:

* Сгенерировать массив – Перегенерирует массив;
* Запустить сортировку – Запускает новую серию сортировки;
* ‘Три горизонтальных полоски’ – Скрывает и раскрывает таблицу с временными данными по сортировкам;
* ‘Иконка графика’ (слева от графика, справа от таблицы) – Скрывает и раскрывает логарифмический график времени по сортировкам, сгруппированных по сериям (каждый новый запуск – новая серия).

В левой части окна (если не скрыта) есть таблица отображающая время сортировки каждым из алгоритмов, алгоритмы с одной серии запускались вместе, в один заход. В правой же части окна располагается таблица отображающая соотношение времени меж алгоритмами в логарифмической форме.
