# Java Kallithea API

Приложение, расширяющее Kallithea API в части работы с Pull requesta'ми.
Позволяет организовать полноценный CI-цикл с CI-сервером, статическим анализом кода и CodeReview на OpenSource решениях. 

Взаимодействие с Kallithea в 99% случаев происходит путем использования одной общей БД. В единичных случаях - по HTTP.

Расширение API Kallithea написано в виде отдельного приложения для возможности разработки нового API без остановки
основной Kallithea.

В качестве технологии была выбрана Java, т.к. здесь авторы имеют больше экспертизы, чем в других технологиях.

## Требования к системе

1. Java 8.
2. Kallithea 0.3.x.
3. PostgreSQL в качестве СУБД для Kallithea. Поддержка других СУБД возможна, но пока не реализована. 

## Запуск

1. После сборки проекта из каталога target нужно взять файлы KalitheaApi-thorntail.jar и settings.yml.
2. В settings.yml нужно задать настройки подключения к БД, API-ключ, путь к репозиториям (описание настроек см. ниже).
3. Сам запуск: java -jar KalitheaApi-thorntail.jar - проверить успешность запуска можно в логе, указанном в settings.yml.

## REST

#### Получить список открытых пул-реквестов для конкретного репозитория

`kalitheaapi/pullrequest/open/{repo}` 

_repo - имя репозитория, в имени репозитория все символы "/" должны быть заменены на символ "."_

Возвращает json-список пар [идентификатор, заголовок] всех не закрытых пул-реквестов репозитория.

**пример:**

запрос: `http://kallitheaapi.example.com/kalitheaapi/pullrequest/open/Consolidation.consolidation`

ответ:
<pre>
[
  {"id":1000854,"title":"Проверить необходимость перезапуска домена при генерации формы"},
  {"id":1001660,"title":"Печать отчетов нижестоящих субъектов (v4)"},
  {"id":1001664,"title":"Версионность отчетов. Смена технической концепции (v3)"},
  {"id":1002108,"title":"Отчетность. Возможность формирования единого протокола проверки без предварительной проверки отчетов (v7)"},
  {"id":1002170,"title":"Аудит. Вернуть аудит в интерфейсы заголовков отчетов"},
  {"id":1002173,"title":"Backdoor. Добавить возможность сохранения файлов (v2)"},
  {"id":1002218,"title":"Рефакторинг движка КС, исправление ошибок сонара (v8)"},
  {"id":1002225,"title":"Справочники. Создать интерфейс для редактирования справочника ОКОПФ"}
]
</pre>

#### Получить детальную информацию о конкретном пул-реквесте

`kalitheaapi/pullrequest/detail/{id}` 

_id - идентификатор пул-реквеста (можно получить из запроса списка открытых пул-реквестов)_

Возвращается полная информация о пул-реквесте в виде json-объекта.

**пример:**

запрос: `http://kallitheaapi.example.com/kalitheaapi/pullrequest/detail/1000618`

ответ:
<pre>
{
    "id":1000618,
    "title":"Тестовый пул-реквест (v2)",
    "description":"Для тестирования плагина сонара\r\n\r\n-- \r\nThis is an update of http://kallithea.example.com/Consolidation/consolidation-external/pull-request/1000613 \"Тестовый пул-реквест\".\r\nНовые ревизии на branch ticket-46077 относительно предыдущего pull-запроса\r\n  2a213620c3da Исправление ошибок Sonar. Тестовый пулреквест для задачи #46077.\r\nAncestor didn't change - show diff since previous version:\r\nhttp://kallithea.example.com/Consolidation/forks/consolidation-external-fork-user1/compare/rev@0a4185a528a8...rev@2a213620c3da",
    "status":"new",
    "createDate":"09/08/2017 09:17",
    "updateDate":"09/08/2017 09:17",
    "user":{"id":11,"name":"user1","email":"user1@example.com","isActive":true},
    "revisions":"0a4185a528a8bf97c7419f402d37855d274f053b:2a213620c3da4ea973d187e907bede010f2418d5",
    "repository":{"id":1000114,"name":"Consolidation/forks/consolidation-external-fork-user1"},
    "orgRef":"branch:ticket-46077:2a213620c3da4ea973d187e907bede010f2418d5",
    "otherRepository":{"id":1000060,"name":"Consolidation/consolidation-external"},
    "otherRef":"branch:default:d0e78fd0874357e0efce0c704f837b7b1e070c8d",
    "reviewers":[{"id":1001013,"status":"under_review","user":"service_sonar"}]}
</pre>

#### Закрыть пул-реквест

`kalitheaapi/pullrequest/{id}/close`

_id - идентификатор пул-реквеста (можно получить из запроса списка открытых пул-реквестов)_

В случае успешного закрытия, ничего не возвращается, в случае ошибки - ошибка.

**пример:**

запрос: `http://kallitheaapi.example.com/kalitheaapi/pullrequest/1000618/close`

#### Получить список пользователей с разрешением на запись для указанного репозитория

`kalitheaapi/pullrequest/permission/{repo}`

_repo - имя репозитория, в имени репозитория все символы "/" должны быть заменены на символ "."_

Возвращается json-список пользователей, обладающих правами на запись в указанный репозиторий.

**пример:**

запрос: `http://kallitheaapi.example.com/kalitheaapi/pullrequest/permission/Consolidation.consolidation`

ответ:
<pre>
[
    {"id":35,"name":"user1","email":"user1@example.com","isActive":true},
    {"id":4,"name":"user2","email":"user2@example.com","isActive":true},
    {"id":1000042,"name":"user3","email":"user3@example.com","isActive":true}]
</pre>

#### Получить права пользователя на указанный репозиторий

`kalitheaapi/pullrequest/user/permission?repo={repo}&user={user}`

_repo - имя репозитория, в имени репозитория все символы "/" должны быть заменены на символ "."_

_user - имя пользователя (в случае отсутствия пользователя в базе - ошибка)_

В случае успеха возвращается право пользователя (в виде строки - text/plain) согласно следующему условию:

- "default" - пользователь существует и имеет права на запись;
- "manager" - пользователь существует и является администратором для данного репозитория;
- "none" - пользователь существует, но не относится к двум предыдущим вариантам.

**пример:**

запрос: `http://kallitheaapi.example.com/kalitheaapi/pullrequest/user/permission?repo=Consolidation.consolidation&user=user1`

ответ:
<pre>
manager
</pre>

#### Добавить ревьювера для пулреквеста

`kalitheaapi/pullrequest/{id}/reviewer/add?user={user}&status={status}`

_id - идентификатор пул-реквеста (можно получить из запроса списка открытых пул-реквестов)_

_user - имя пользователя (в случае отсутствия пользователя в базе - ошибка)_

_status - статус у ревьювера для пул-реквеста (необязательный параметр) - по умолчанию имеет значение "under_review"_

В случае успешного добавления (либо если такой ревьювер уже есть) ничего не возвращает, иначе ошибка.

**пример:**

запрос: `http://kallitheaapi.example.com/kalitheaapi/pullrequest/23/reviewer/add?user=user1&status=rejected`

#### Получить путь к родительскому репозиторию

`kalitheaapi/repository/parent/{repo}`

_repo - имя подчиненного репозитория, в имени репозитория все символы "/" должны быть заменены на символ "."_

Ответ возвращается в виде строки (text/plain). В случае если репозиторий не найден, либо нет родительского репозитория - возвращается ошибка.

**пример:**

запрос: `http://kallitheaapi.example.com/kalitheaapi/repository/parent/Planning.knowledge-91`

ответ:
<pre>
Planning/knowledge
</pre>

#### Получить путь к подчиненным репозиториям

`kalitheaapi/repository/childs/{repo}?filter={filler}`

_repo - имя родительского репозитория, в имени репозитория все символы "/" должны быть заменены на символ "."_

_filter - фильтр (необязательный параметр) в виде регулярного выражения. Служит для отсеивания репозиториев, неудовлетворяющим заданым правилам именования_

Возвращается массив всех подчиненных репозиториев (в случае задания фильтра - всех репозиториев с именем удовлетворяющим этому фильтру).

**пример:**

запрос: `http://kallitheaapi.example.com/kalitheaapi/repository/childs/Planning.knowledge`

ответ:
<pre>
[
    "Planning/knowledge-region1",
    "Planning/knowledge-region2",
    "Planning/knowledge-region3"
]
</pre>

#### Получить список открытых бранчей репозитория

`kalitheaapi/repository/branches/{repo}`

_repo - имя репозитория, в имени репозитория все символы "/" должны быть заменены на символ "."_

Возвращается json-список открытых бранчей в виде пар [имя, идентификатор].

**пример:**

запрос: `http://kallitheaapi.example.com/kalitheaapi/repository/branches/Planning.knowledge`

ответ:
<pre>
[
    {"name":"1.11.*-RELEASE","id":"56a38ce5493f4bd4ef403afbd6c5e871de4cd120"},
    {"name":"1.12.*-82N_GlobalID","id":"be30e46f1407145aa51d5515613f61069a950d07"},
    {"name":"1.12.*-RELEASE","id":"414bf60fad45c6b13d6aa97df24377e1b616947a"},
    {"name":"default","id":"ab618aa0364a532b09a01348cba48ae0f1159c20"}
]
</pre>

#### Заблокировать репозиторий от добавления новых ревизий на время релиза

`kalitheaapi/repository/lock?repo={repo}&user={user}`

_repo - имя репозитория, в имени репозитория все символы "/" должны быть заменены на символ "."_

_user - имя пользователя (в случае отсутствия пользователя в базе - ошибка)_

В случае отсутствия ошибок ответ в виде строки (plain/text) "OK", иначе сообщение об ошибке.

Блокировка репозитория заключается в добавлении маркерного файла в каталог .hg. Сам маркерный файл содержит имя пользователя-владельца (установщика) блокировки.

Возможные ошибки:

- отсутствие пользователя или репозитория в базе;
- не указание/не существование каталога репозиториев или каталога .hg (подразумевается что относительно корневого каталог репозиториев сами репозитории расположены в каталогах согласно их имени);
- существование файла блокировки;
- отсутствие админских прав у пользователя.

**пример:**

запрос: `http://kallitheaapi.example.com/kalitheaapi/repository/lock?repo=Consolidation.consolidation-external&user=user1`

ответ:
<pre>
OK
</pre>

#### Разблокировать заблокированный от добавления новых ревизий на время релиза репозиторий

`kalitheaapi/repository/unlock?repo={repo}&user={user}`

_repo - имя репозитория, в имени репозитория все символы "/" должны быть заменены на символ "."_

_user - имя пользователя (в случае отсутствия пользователя в базе - ошибка)_

В случае отсутствия ошибок ответ в виде строки (plain/text) "OK", иначе сообщение об ошибке.

Разблокировка репозитория заключается в удалении маркерного файла в каталоге .hg.

Возможные ошибки:

- отсутствие пользователя или репозитория в базе;
- не указание/не существование каталога репозиториев или каталога .hg (подразумевается что относительно корневого каталог репозиториев сами репозитории расположены в каталогах согласно их имени);
- не существование файла блокировки в случае разблокировки;
- не совпадение имен пользователей.

**пример:**

запрос: `http://kallitheaapi.example.com/kalitheaapi/repository/unlock?repo=Consolidation.consolidation-external&user=user1`

ответ:
<pre>
OK
</pre>

#### Добавить комментарий сонар-пользователя с установкой статуса и отправкой сообщения на почту пользователю-владельцу пул-реквеста

Единственный POST-запрос: `pullrequest/sonar/report/{id}?user={user}&clean={clean}`

_id - идентификатор пул-реквеста (можно получить из запроса списка открытых пул-реквестов)_

_user - имя сонар-пользователя (в случае отсутствия пользователя в базе - ошибка)_

_clean - признак необходимости очистки предыдущих комментариев сонар-пользователя для пул-реквеста (по умолчанию имеет значение true)_

В теле запроса передаются параметры задания комментария:
<pre>
{
    "line": [{"order": "1", "message": "line comment 1", "line": "n35", "file": "testClass.java"}, {"order": "2", "message": "line comment 2", "line": "n107", "file": "testClass2.java"}],
    "common": "common comment",
    "status": "rejected",
    "mailInfo": {"ruStatus": "Отклонено", "body": "mail body"}
}
</pre>

_line - линейные комментарии для измененных файлов пул-реквеста_

_common - общий комментарий_

_status - статус сонар-пользователя при добавлении комментария (сонар пользователь - ревьювер)_

_mailInfo - информация для почтового отправления_

Этот запрос сейчас используется только совместно с плагином сонара sonar-kallithea.

## Settings.yml

 - swarmlog - путь к логу (обязательный параметр) - содержит логи по запуску и ошибкам запросов.
 - databaseurl - строка подключение к базе kallithea (обязательный параметр), пример строки подключения для postgres - `jdbc:postgresql://localhost:5432/kallithea_2018`.
 - dblogin - логин для подключения к базе kallithea (обязательный параметр).
 - dbpassword - пароль для подключения к базе kallithea (обязательный параметр).
 - httpport - порт для разворачивания приложения (необязательный параметр, по умолчанию 8081).
 - managementport - порт управления (необязательный параметр, по умолчанию 9995).
 - mailhost - почтовый хост (необязательный параметр), нужен исключительно для запросов сонар-пользователя, для отправки сообщения пользователю-владельцу пул-реквеста.
 - mailport - почтовый порт (необязательный параметр), нужен исключительно для запросов сонар-пользователя, для отправки сообщения пользователю-владельцу пул-реквеста.
 - mailfrom - отправитель (обязателен, если указан параметр mailhost).
 - mailuser - имя пользователя для почтового хоста (обязателен, если указан параметр mailhost).
 - mailpass - пароль пользователя для почтового хоста (обязателен, если указан параметр mailhost).
 - mailssl - признак ssl для почтового хоста (необязательный параметр, по умолчанию false).
 - maildebug - включение возможности отладки при отправке писем (необязательный параметр, по умолчанию false).
 - kallithea.home - url к самой kallithea (обязательный параметр), используется при запросах открытых бранчей репозитория.
 - kallithea.lock - имя файла блокировки/разблокировки репозитория на время релиза (необязательный параметр, по умолчанию - kallithea.lock).
 - kallithea.user.apiKey - apiKey пользователя kallithea, используется для получаения списка открытых бранчей репозитория.
 - repository.home - путь к каталогу размещения репозиториев kallithea, используется при запросах блокирования/разблокирования репозитория на время релиза. 
 
 
 