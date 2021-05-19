import Battlefield from "./Battlefield";
import Hand from "./Hand";

export default function ZoneLayout() {
    return <div className="d-flex flex-column h-100">
        <div className="flex-grow-1">
            <Battlefield/>
        </div>
        <Hand className="my-2" />
    </div>
}
