import Hand from "./Hand";

export default function ActiveGameController() {
    return <div className="d-flex flex-column h-100">
        <div className="flex-grow-1"/>
        <Hand className="mb-2" />
    </div>
}
