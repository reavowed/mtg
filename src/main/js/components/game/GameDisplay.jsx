import EventLog from "./EventLog";
import OpeningHand from "./openingHand/OpeningHand";

export default function GameDisplay() {
    return <div class="row">
        <div class="col-md-9">
            <OpeningHand />
        </div>
        <div class="col-md-3">
            <EventLog />
        </div>
    </div>;
}
