package jp.co.cyberagent.itemae.domain.repository

import jp.co.cyberagent.itemae.domain.model.entity.Token
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenReponsitory : JpaRepository<Token, Long> {
}