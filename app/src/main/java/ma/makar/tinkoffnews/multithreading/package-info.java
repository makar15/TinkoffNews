@FieldsNonNullByDefault
package ma.makar.tinkoffnews.multithreading;

import ma.makar.base.FieldsNonNullByDefault;

/*
 Класс ExecutorServiceBuilder позволяет создать новый ExecutorService с определенным размером
 потоков в пуле и кастомной фабрикой потоков (ThreadFactory).
 NamedThreadFactory - позволяет именовать потоки, что создает удобство просмотра
 состояния потока при логгировании.
 */
