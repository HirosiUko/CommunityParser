import io.reactivex.rxjava3.disposables.Disposable

fun main(args: Array<String>) {
    // Board Test
    var t1 = QueryEngine().queryBoard("새로운소식")
    t1.subscribe(
        /* onSuccess = */
        fun(it: List<Any>) {
            for (a in it) {
                println(a)
            }
        },
    )

    // Article Test
    var t2 = QueryEngine().queryArticle()
    t2.subscribe(
        /* onSuccess = */
        fun(it: List<ArticleVo>) {
            for (a in it) {
                println(a.toString())
            }
        },
    )
}