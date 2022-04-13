package com.yingenus.pocketchinese.domain.dto

class KnownLevel(val level : Int) {
    companion object{

        private const val MAX_LVL = 10
        private const val MIN_LVL = 0

        val maxLevel : KnownLevel = KnownLevel(MAX_LVL)
        val minLevel : KnownLevel = KnownLevel( MIN_LVL)

        fun creteSafe(level: Int) : KnownLevel = KnownLevel( Math.min(Math.max(level, MIN_LVL),
            MAX_LVL))
    }

    init {
        if ( level > MAX_LVL || level < MIN_LVL)
            throw IllegalStateException( " level cant be more then $MAX_LVL and less then $MIN_LVL")
    }

    fun nextLevel() = KnownLevel( Math.min(level+1,MAX_LVL))
    fun previousLevel() = KnownLevel ( Math.max( level -1, MIN_LVL))

    fun inc() = nextLevel()
    fun dec() = previousLevel()

    fun compereTo( knownLevel: KnownLevel) : Int = this.level.compareTo(knownLevel.level)


    override fun equals(other: Any?): Boolean {
        return if ( other === this) true
        else if (other is KnownLevel) other.level == this.level
        else false
    }

    override fun hashCode(): Int {
        return level.hashCode()
    }
}