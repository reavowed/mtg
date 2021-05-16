
export function getPlural(number, singularWord, pluralWord) {
    if (number === 1) {
        return "a " + singularWord;
    } else {
        return number + " " + pluralWord;
    }
}
