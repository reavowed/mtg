import Hand from "../Hand";
import FirstPlayerMessage from "./FirstPlayerMessage";
import MulliganDecision from "./MulliganDecision";
import OpponentMulliganMessage from "./OpponentMulliganMessage";


export default function OpeningHand() {
    return <div>
        <FirstPlayerMessage />
        <Hand />
        <OpponentMulliganMessage />
        <MulliganDecision />
    </div>;
}
