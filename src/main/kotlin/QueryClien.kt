import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Throws(NoSuchAlgorithmException::class, KeyManagementException::class)
fun setSSL() {
    val trustAllCerts = arrayOf<TrustManager>(
        object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? {
                // TODO Auto-generated method stub
                return null
            }

            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                // TODO Auto-generated method stub
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                // TODO Auto-generated method stub
            }
        }
    )
    val sc = SSLContext.getInstance("SSL")
    sc.init(null, trustAllCerts, SecureRandom())
    HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
}

class QueryEngine {
    private val boardNameMap = mapOf<String, String>(
        "모두의공원" to "https://m.clien.net/service/board/park",
        "새로운소식" to "https://m.clien.net/service/board/news",
        "알뜰구매" to "https://m.clien.net/service/board/jirum"
    )

    private val MAIN_SELECTOR: String
        get() = "body > div.nav_container > div.content_list > a:nth-child(n)"

    private val TITLE_SELECTOR: String
        get() = "div.list_title > div.list_subject > span:nth-child(2)"

    fun queryBoard(title: String = "모두의공원"): Single<List<Any>> {
        var url = boardNameMap[title]
        return Single.fromObservable(Observable.create {
            val BoardList: ArrayList<Any> = ArrayList()
            setSSL()
            val doc: Document = Jsoup.connect(url).userAgent("Mozilla").get()
            val contentElements: Elements = doc.select(MAIN_SELECTOR) // title, link
            for ((i, elem) in contentElements.withIndex()) {
                val id = elem.attr("data-board-sn")
                val title = elem.select(TITLE_SELECTOR).text()
                val author = elem.attr("data-author-id")
                val commentCnt = elem.attr("data-comment-count")
                val link = elem.attr("href")
                println("-> ${id}, ${title}, ${author}, ${commentCnt}, https://m.clien.net$link")
                if(id.isNotEmpty()) BoardList += mapOf<String,String>(
                    "id" to id,
                    "author" to author,
                    "title" to title,
                    "commentCnt" to commentCnt,
                    "link" to link
                )
            }
            it.onNext(BoardList)
            it.onComplete()
        })
    }

    fun queryArticle(url: String = "https://m.clien.net/service/board/news/17884635?od=T31&po=0&category=0&groupCd="): Single<List<Any>> {
        return Single.fromObservable(Observable.create {
            val BoardList: ArrayList<Any> = ArrayList()
            setSSL()
            val doc: Document = Jsoup.connect(url).userAgent("Mozilla").get()
            val contentElements: Elements = doc.select("body > div.nav_container > div.content_view > div.post_view > div.post_content > article") // title, link
//            for ((i, elem) in contentElements.withIndex()) {
//                val id = elem.attr("data-board-sn")
//                val title = elem.select(TITLE_SELECTOR).text()
//                val author = elem.attr("data-author-id")
//                val commentCnt = elem.attr("data-comment-count")
//                val link = elem.attr("href")
//                println("-> ${id}, ${title}, ${author}, ${commentCnt}, https://www.clien.net$link")
//                if(id.isNotEmpty()) BoardList += mapOf<String,String>(
//                    "id" to id,
//                    "author" to author,
//                    "title" to title,
//                    "commentCnt" to commentCnt,
//                    "link" to link
//                )
//            }
            it.onNext(BoardList)
            it.onComplete()
        })
    }

//    fun requestMoreNotice(offset: Int) = Single.fromObservable(
//        Observable.create {
////                val bachNoticeList: ArrayList<BachelorNotice> = ArrayList()
//            val doc: Document =
//                Jsoup.connect("https://computer.cnu.ac.kr/computer/notice/bachelor.do?mode=list&&articleLimit=10&article.offset=$offset")
//                    .get() // Base Url
//            val contentElements: Elements =
//                doc.select("div[class=b-title-box]").select("a") // title, link
//            val idElements: Elements = doc.select("td[class=b-num-box]") // id값
//            for ((i, elem) in contentElements.withIndex()) {
//                if (idElements[i].text() != "공지") { // 공지는 매 페이지마다 있으므로 중복제거
//                    println("Program arguments: ${idElements[i].text()}, ${elem.text()},${elem.attr("href")}")
////                        println(idElements[i].text(),
////                            elem.text(),
////                            elem.attr("href"))
////                        bachNoticeList.add(
////                            BachelorNotice(
////                                idElements[i].text(),
////                                elem.text(),
////                                elem.attr("href")
////                            )
////                        )
//                }
//            }
////                it.onNext(bachNoticeList)
////                it.onComplete()
//        }
//    )
}