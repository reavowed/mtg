import Battlefield from "./Battlefield";
import Hand from "./Hand";
import Stack from "./Stack";

export default function ZoneLayout() {
    return <div className="d-flex flex-column h-100">
        <div className="flex-grow-1 border-bottom">
            <div class="d-flex h-100">
                <div className="flex-grow-1">
                    <Battlefield/>
                </div>
                <div>
                    <Stack />
                </div>
            </div>
        </div>
        <Hand className="my-2" />
    </div>
}
