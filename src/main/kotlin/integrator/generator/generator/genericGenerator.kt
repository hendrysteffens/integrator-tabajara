package integrator.generator.generator

fun convertInitialLetter(text: String): String {
    return text.trim().substring(0, 1).toUpperCase().plus(text.trim().substring(1));
}

fun removeSpaceAndSpecialCharacteres(string: String): String {
    return Regex("[^A-Za-z0-9]").replace(string, "");
}

