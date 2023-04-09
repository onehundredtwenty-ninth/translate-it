# Translate it

ER-диаграмма: [ER](https://dbdiagram.io/d/642e0f4d5758ac5f17271806)

В качестве сервиса для перевода был выбран [MyMemory](https://mymemory.translated.net/). Это обосновано тем, что 
для его использования нет необходимости регистрироваться или привязывать карту для получения токена

Запуск в контейнере
````
docker build -t translate_service_image .
docker run --name translate_service_container -p 8080:8080 translate_service_image