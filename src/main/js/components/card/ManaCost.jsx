export default function ManaCost({manaCost}) {
    if (manaCost) {
        const symbolContents = manaCost.split(/(?={)/).map(s => s.replace(/[{}]/g, "").toUpperCase());
        return symbolContents.map(s => <span className={"card-symbol card-symbol-" + s}/>)
    }
}
