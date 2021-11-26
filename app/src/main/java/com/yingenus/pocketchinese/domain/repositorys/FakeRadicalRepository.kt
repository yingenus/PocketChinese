package com.yingenus.pocketchinese.domain.repositorys

class FakeRadicalRepository : CharactersRadicalRepository {
    override fun getRadicals(): Map<Int, List<String>> {
        return mapOf(
                1 to listOf("A","B","C","D","E","F","G","H"),
                2 to listOf("I","G","K","L","M","N","O","P","R","S","T","U","F","A","B","C","D","E","F","G","H","F","G","H","I","G","K","L","M","N","O"),
                3 to listOf("A","C","D","E","F","G","H","I","G","K","L","M","B","C","D","E","F","G","H","I","G","K","L","M","N","O","P","R","S","T","U","F"),
                4 to listOf("G","H","I","G","K","L","M","N","O","P","R","S","T","U","F"),
                5 to listOf("A","B","C","L","M","N","O","P","R","S","T","U","F"),
                6 to listOf("A","B","C","D","E","F","G","H","I","G","K","S","T","U","F"),
                7 to listOf("A","B","C","P","R","S","T","U","F"),
                8 to listOf("N","O","P","R","S","T","U","F")
        )
    }

    override fun getCharacters(radical: String): Map<Int, List<String>> {
        return mapOf(
                1 to listOf("G","H","I","G","K","L","M","N","O","P","R","S","T","U","F"),
                2 to listOf("A","B","C","D","E","F","G","H","I","G","K","S","T","U","F"),
                3 to listOf("A","B","C","P","R","S","T","U","F"),
                4 to listOf("A","B","C","D","E","F","G","H"),
                5 to listOf("N","O","P","R","S","T","U","F"),
                6 to listOf("I","G","K","L","M","N","O","P","R","S","T","U","F","A","B","C","D","E","F","G","H","F","G","H","I","G","K","L","M","N","O"),
                7 to listOf("A","C","D","E","F","G","H","I","G","K","L","M","B","C","D","E","F","G","H","I","G","K","L","M","N","O","P","R","S","T","U","F"),
                8 to listOf("A","B","C","L","M","N","O","P","R","S","T","U","F")
        )
    }
}