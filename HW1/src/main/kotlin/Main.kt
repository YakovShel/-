// Импорты необходимых библиотек и классов Java и Java.time для работы с временем
import java.util.Scanner
import java.util.*
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Класс представляет фильм, имеющий название и продолжительность
data class Movie(val title: String, val duration: Int)

// Класс Session хранит информацию о фильме, времени сеанса и состоянии мест (занято/свободно)
data class Session(val movie: Movie, val time: String, val seats: MutableList<Int> = MutableList(10) { 0 })

// Класс Cinema представляет собой кинотеатр с методами для работы с сеансами и билетами
class Cinema {
    // Внутренние коллекции для хранения информации о фильмах и сеансах
    internal val movies = mutableListOf<Movie>()
    internal val sessions = mutableListOf<Session>()

    // Методы для добавления фильма и сеанса в кинотеатр
    fun addMovie(movie: Movie) {
        movies.add(movie)
    }

    fun addSession(session: Session) {
        sessions.add(session)
    }

    // Метод проверяет, прошло ли время сеанса
    fun isBeforeSessionTime(sessionIndex: Int): Boolean {
        // Форматирование времени и проверка, прошло ли время сеанса
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val currentTime = LocalTime.now()
        val sessionTime = try {
            LocalTime.parse(sessions[sessionIndex].time, formatter)
        } catch (e: DateTimeParseException) {
            println("Ошибка в формате времени сеанса.")
            return false
        }

        return currentTime.isBefore(sessionTime)
    }

    // Продажа билета для указанного сеанса и места
    fun sellTicket(sessionIndex: Int, seatNumber: Int) {
        // Проверка существования сеанса и возможности продажи билета
        if (sessionIndex in sessions.indices) {
            if (!isBeforeSessionTime(sessionIndex)) {
                println("Продажа билетов для этого сеанса уже завершена.")
                return
            }

            val session = sessions[sessionIndex]
            if (seatNumber in session.seats.indices) {
                if (session.seats[seatNumber - 1] == 0) {
                    session.seats[seatNumber - 1] = 1
                    println("Билет на место $seatNumber для фильма ${session.movie.title} в ${session.time} продан")
                } else {
                    println("Место $seatNumber уже занято")
                }
            } else {
                println("Неверный номер места")
            }
        } else {
            println("Неверный номер сеанса")
        }
    }

    // Возврат билета для указанного сеанса и места
    fun returnTicket(sessionIndex: Int, seatNumber: Int) {
        // Проверка существования сеанса и возможности возврата билета
        if (sessionIndex in sessions.indices) {
            if (!isBeforeSessionTime(sessionIndex)) {
                println("Возврат билетов для этого сеанса уже завершен.")
                return
            }

            val session = sessions[sessionIndex]
            if (session.seats[seatNumber - 1] == 1) {
                session.seats[seatNumber - 1] = 0
                println("Билет на место $seatNumber для фильма ${session.movie.title} в ${session.time} возвращен")
            } else {
                println("Место $seatNumber уже свободно")
            }
        } else {
            println("Неверный номер сеанса")
        }
    }

    // Отображение состояния занятости мест для указанного сеанса
    fun displaySeats(sessionIndex: Int) {
        // Вывод информации о свободных и занятых местах на сеансе
        val session = sessions[sessionIndex]
        println("Свободные места для сеанса ${session.movie.title} в ${session.time}:")
        session.seats.forEachIndexed { index, status ->
            if (status == 0) {
                println("Место ${index + 1}: Свободно")
            } else {
                println("Место ${index + 1}: Занято")
            }
        }
    }

    // Метод для редактирования названия фильма
    fun editMovieTitle(index: Int, newTitle: String) {
        // Изменение названия фильма и его обновление в сеансах
        val oldTitle = movies[index].title
        movies[index] = movies[index].copy(title = newTitle)

        sessions.forEachIndexed { sessionIndex, session ->
            if (session.movie.title == oldTitle) {
                val updatedMovie = session.movie.copy(title = newTitle)
                sessions[sessionIndex] = session.copy(movie = updatedMovie)
            }
        }
    }

    // Метод для редактирования времени сеанса
    fun editSessionTime(index: Int, newTime: String) {
        // Изменение времени сеанса для указанного индекса
        sessions[index] = sessions[index].copy(time = newTime)
    }

    // Отметка занятых мест на сеансе
    fun markOccupiedSeats(sessionIndex: Int, seatNumbers: List<Int>) {
        // Отметка указанных мест как занятых на сеансе
        val session = sessions[sessionIndex]
        seatNumbers.forEach { seatNumber ->
            if (session.seats[seatNumber - 1] == 0) {
                session.seats[seatNumber - 1] = 1
            }
        }
        println("Места $seatNumbers отмечены как занятые для сеанса ${session.movie.title} в ${session.time}")
    }

    // Получение списка сеансов
    fun getSessions(): List<Session> {
        return sessions
    }

    // Получение списка фильмов
    fun getMovies(): List<Movie> {
        return movies
    }
}


fun main() {
    while (true) {
        try {
            runCinemaProgram()
            break

        } catch (e: InputMismatchException) {
            println("Ошибка ввода данных. Пожалуйста, введите корректное значение.")
        } catch (e: NoSuchElementException) {
            println("Элемент не найден. Пожалуйста, проверьте правильность ввода.")
        } catch (e: IllegalStateException) {
            println("Неверное состояние. Пожалуйста, проверьте правильность последовательности действий.")
        } catch (e: Exception) {
            println("Произошла непредвиденная ошибка: ${e.message}.")
            e.printStackTrace()
        }
    }
}

fun runCinemaProgram() {
    // Создание экземпляра кинотеатра и объекта для считывания данных с консоли
    val cinema = Cinema()
    val scanner = Scanner(System.`in`)

    // Создание пяти разных фильмов с установленными временами сеансов
    val movies = listOf(
        Movie("Film 1", 120),
        Movie("Film 2", 105),
        Movie("Film 3", 90),
        Movie("Film 4", 135),
        Movie("Film 5", 100)
    )

    // Установка времени сеансов для фильмов
    val sessionTimes = listOf("11:00", "13:30", "15:30", "18:00", "20:30")

    // Добавление сеансов в кинотеатр для каждого фильма
    movies.forEachIndexed { index, movie ->
        val session = Session(movie, sessionTimes[index])
        cinema.addSession(session)
    }

    // Основной цикл, предоставляющий пользователю меню с выбором действий
    var choice = 0
    while (choice != 6) {
        // Вывод пользовательского меню
        println("Выберите действие:")
        println("1. Продажа билета")
        println("2. Возврат билета")
        println("3. Просмотр свободных/занятых мест на сеансе")
        println("4. Редактирование данных о фильмах и сеансах")
        println("5. Отметка занятых мест на сеансе")
        println("6. Выход и сохранение конечных данных")

        // Считывание выбора пользователя
        choice = scanner.nextInt()

        // Обработка выбора пользователя в соответствии с выбранным действием
        when (choice) {
            1 -> {
                // Продажа билета
                // Пользователь выбирает сеанс и место для покупки билета
                println("Выберите сеанс для продажи билетов:")
                cinema.getSessions().forEachIndexed { index, session ->
                    println("${index + 1}. ${session.movie.title} в ${session.time}")
                }
                val sessionIndex = scanner.nextInt()

                if (sessionIndex in 1..cinema.getSessions().size) {
                    println("Выберите номер места:")
                    cinema.displaySeats(sessionIndex - 1)
                    val seatNumber = scanner.nextInt()

                    cinema.sellTicket(sessionIndex - 1, seatNumber)
                } else {
                    println("Неверный выбор сеанса.")
                }
            }

            2 -> {
                // Возврат билета
                // Пользователь выбирает сеанс и место для возврата билета
                println("Выберите сеанс для возврата билета:")
                cinema.getSessions().forEachIndexed { index, session ->
                    println("${index + 1}. ${session.movie.title} в ${session.time}")
                }
                val sessionIndex = scanner.nextInt()

                if (sessionIndex in 1..cinema.getSessions().size) {
                    println("Выберите номер места для возврата:")
                    cinema.displaySeats(sessionIndex - 1)
                    val seatNumber = scanner.nextInt()

                    val session = cinema.getSessions()[sessionIndex - 1]
                    if (seatNumber in 1..session.seats.size) {
                        cinema.returnTicket(sessionIndex - 1, seatNumber)
                    } else {
                        println("Неверный номер места для данного сеанса.")
                    }
                } else {
                    println("Неверный выбор сеанса.")
                }
            }

            3 -> {
                // Просмотр состояния мест на сеансе
                // Пользователь выбирает сеанс для просмотра свободных/занятых мест
                println("Выберите сеанс для просмотра мест:")
                cinema.getSessions().forEachIndexed { index, session ->
                    println("${index + 1}. ${session.movie.title} в ${session.time}")
                }
                val sessionIndex = scanner.nextInt()

                if (sessionIndex >= 0 && sessionIndex < cinema.getSessions().size) {
                    cinema.displaySeats(sessionIndex)
                } else {
                    println("Неверный выбор сеанса.")
                }
            }
            4 -> {
                // Редактирование данных о фильмах и сеансах
                // Пользователь выбирает тип данных для редактирования
                println("Выберите тип данных для редактирования:")
                println("1. Данные о фильмах")
                println("2. Расписание сеансов")
                val editChoice = scanner.nextInt()

                when (editChoice) {
                    1 -> {
                        // Вывод списка фильмов для выбора
                        println("Список фильмов:")
                        cinema.getSessions().forEachIndexed { index, session ->
                            println("${index + 1}. ${session.movie.title}")
                        }

                        println("Выберите фильм для редактирования:")
                        val movieIndex = scanner.nextInt()

                        if (movieIndex in 1..cinema.getMovies().size) {
                            println("Введите новое название фильма:")
                            val newTitle = scanner.next()
                            cinema.editMovieTitle(movieIndex - 1, newTitle)
                        } else {
                            println("Неверный выбор фильма.")
                        }
                    }
                    2 -> {
                        println("Выберите сеанс для редактирования:")
                        cinema.getSessions().forEachIndexed { index, session ->
                            println("${index + 1}. ${session.movie.title} в ${session.time}")
                        }
                        val sessionIndex = scanner.nextInt()

                        if (sessionIndex in 1..cinema.getSessions().size) {
                            println("Введите новое время для сеанса:")
                            val newTime = scanner.next()
                            cinema.editSessionTime(sessionIndex - 1, newTime)
                        } else {
                            println("Неверный выбор сеанса.")
                        }
                    }
                    else -> {
                        println("Неверный выбор. Попробуйте снова.")
                    }
                }
            }

            5 -> {
                // Отметка занятых мест на сеансе
                // Пользователь выбирает сеанс и места для отметки как занятые
                println("Выберите сеанс для отметки занятых мест:")
                cinema.getSessions().forEachIndexed { index, session ->
                    println("${index + 1}. ${session.movie.title} в ${session.time}")
                }
                val sessionIndex = scanner.nextInt()

                if (sessionIndex in 1..cinema.getSessions().size) {
                    println("Введите количество мест для отметки:")
                    val numberOfSeats = scanner.nextInt()

                    val seats = mutableListOf<Int>()
                    repeat(numberOfSeats) {
                        println("Введите номер места:")
                        seats.add(scanner.nextInt())
                    }

                    cinema.markOccupiedSeats(sessionIndex - 1, seats)
                } else {
                    println("Неверный выбор сеанса.")
                }
            }

            6 -> {
                // Выход и сохранение данных в файл
                // Программа завершается, а данные сохраняются в формате CSV
                println("Сохранение данных...")

                val sessionsCSV = File("sessions.csv")
                sessionsCSV.bufferedWriter(StandardCharsets.UTF_8).use { out ->
                    cinema.getSessions().forEach { session ->
                        val seats = session.seats.joinToString(",")
                        out.write("${session.movie.title},${session.time},$seats\n")
                    }
                }

                println("Данные успешно сохранены в формате CSV в файлах sessions.csv.")
            }
            else -> {
                // Выводится при некорректном выборе пользователя
                println("Неверный выбор. Попробуйте снова.")
            }
        }
    }
}