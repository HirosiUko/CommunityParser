class ArticleVo (
    val articleTitle: String,
    val articleText: String,
    val authorNickName: String,
    val articleDateTime: String,
    val articleViewCount: String,
    val articleFavoriteCount: String
) {
    var comments = mutableListOf<CommentVo>()

    override fun toString(): String {
        return "title: $articleTitle,\n text: $articleText,\n NickName:$authorNickName,\n DateTime: $articleDateTime,\n ViewCount: $articleViewCount,\n FavoriteCount: $articleFavoriteCount \n $comments"
    }
}

class CommentVo (val commentAuthor: String, val commentDateTime: String, val commentText: String) {
    override fun toString(): String {
        return "<< $commentAuthor, $commentDateTime, $commentText >>\n"
    }
}

class Board(val id: String, val title: String, val author: String, val commentCnt: String, val link: String) {
    override fun toString(): String = "$id, $title, $author, $commentCnt, $link"
}