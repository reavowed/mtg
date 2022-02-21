export function ActionText({text}) {
    const elements = [];
    while (text !== "") {
        if (text.startsWith("{")) {
            const closingBraceIndex = text.indexOf("}")
            const symbolContents = text.substring(1, closingBraceIndex);
            elements.push(<span className={"card-symbol card-symbol-" + symbolContents}/>);
            text = text.substring(closingBraceIndex + 1);
        } else {
            const openingBraceIndex = text.indexOf("{");
            if (openingBraceIndex > -1) {
                elements.push(text.substring(0, openingBraceIndex))
                text = text.substring(openingBraceIndex);
            } else {
                elements.push(text);
                text = "";
            }
        }
    }
    return elements;
}
