import HorizontalCenter from "../../layout/HorizontalCenter";
import DecisionButton from "../DecisionButton";

export default function PriorityChoice() {
    return <div>
        <p>Cast spells, activate abilities, or play a land.</p>
        <HorizontalCenter>
            <DecisionButton optionToChoose="Pass">Pass</DecisionButton>
        </HorizontalCenter>
    </div>;
}
