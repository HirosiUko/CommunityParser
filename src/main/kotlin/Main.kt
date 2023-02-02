import io.reactivex.rxjava3.disposables.Disposable

fun main(args: Array<String>) {
    var t = QueryEngine().queryBoard()
    var subscribe = t.subscribe(
        /* onSuccess = */
        fun(it: List<Any>) {
            for (a in it) {
                println(a)
            }
        },
    )
}