
export function getPlural(number, singularWord, pluralWord) {
    if (number === 1) {
        return number + " " + singularWord;
    } else {
        return number + " " + pluralWord;
    }
}
