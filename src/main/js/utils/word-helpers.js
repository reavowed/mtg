
export function getPlural(number, singularWord, pluralWord) {
    if (number === 1) {
        return number + " " + singularWord;
    } else {
        return number + " " + pluralWord;
    }
}

export function commaList(words) {
    if (words.length === 1) {
        return words[0];
    } else {
        return words.slice(0, words.length - 1).join(", ") + " and " + words[words.length - 1];
    }
}
