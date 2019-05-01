<h2> Ahorcado: Segundo Trabajo Práctico de Laboratorio 5 de TSSI - Threads

<br><h3> Diferencia entre Runnable y Thread
<h5> La principal diferencia entre Runnable y Thread es que al crear multiples hilos extendiendo de Thread
 cada vez que se cree un hilo se creará un objeto y un hilo se asociará a este. En cambio al crearlos
 implementando Runnable los diestintos hilos pueden asociarse a un mismo objeto.

<br><h3> Ciclo de Vida de un Thread
<h5> Un thread a lo largo de su vida pasa por 5 diferentes etapas:
<br><h4> New<h5>El thread se encuentra en esta etapa en el intervalo que se encuentra entre que es creado y el momento en el que es inicializado - start().
<br><h4> Runnable<h5>El thread se encuentra en este estado cuando el método start() fue invocado pero el planificador todavía no lo seleccionó para correr.
<br><h4> Running<h5>El thread se encuentra en este estado cuando el planificador lo elige para ser ejecutado.
<br><h4> Non-Runnable<h5>El thread se encuentra en este estado cuando sigue vivo pero el planificador lo bloquea.
<br><h4> Terminated<h5>El thread se encuentra en este estado (se muere) una vez que terminó de correr su método run().

<br><h3> Métodos de un Thread
<br><h4> Start<h5> Crea un stack aparte para el thread y luego la JVM llama al método run().
<br><h4> Sleep<h5> Indica al planificador que "duerma" al thread por cierta cantidad de tiempo determinada en milisegundos.
<br><h4> Yield<h5> Este método le avisa al planificador que el thread no es de mucha importancia y que por ende si necesita correr otro thread e interrumpir este, puede hacerlo.
<br><h4> Join<h5> El método indica que el thread debe quedarse esperando (bloquedo) hasta que otro thread termine su ejecución.
