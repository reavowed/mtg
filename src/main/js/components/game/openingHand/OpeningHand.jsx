import ScreenCenter from "../../layout/ScreenCenter";
import Hand from "../Hand";
import FirstPlayerMessage from "./FirstPlayerMessage";
import MulliganDecision from "./MulliganDecision";
import OpponentMulliganMessage from "./OpponentMulliganMessage";


export default function OpeningHand() {
    return <ScreenCenter>
        <div>
            <FirstPlayerMessage />
            <Hand />
            <OpponentMulliganMessage />
            <MulliganDecision />
        </div>
    </ScreenCenter>;
}
