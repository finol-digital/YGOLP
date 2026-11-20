import Testing
@testable import YGOLP

struct CalculatorTests {
    @Test func startsAtZeroInRequestedMode() {
        let calculator = Calculator(lifePoints: 8000, mode: .subtract)
        #expect(calculator.operandText == "0")
        #expect(calculator.mode == .subtract)
        #expect(calculator.result == 8000)
    }

    @Test func appendsDigitsReplacingLeadingZero() {
        var calculator = Calculator(lifePoints: 8000)
        calculator.append("1")
        calculator.append("000")
        #expect(calculator.operandText == "1000")
    }

    @Test func zerosOnZeroStayZero() {
        var calculator = Calculator(lifePoints: 8000)
        calculator.append("00")
        calculator.append("000")
        #expect(calculator.operandText == "0")
    }

    @Test func operandIsLimitedToFiveDigits() {
        var calculator = Calculator(lifePoints: 8000)
        calculator.append("12")
        calculator.append("000")
        calculator.append("000")
        #expect(calculator.operandText == "12000")
    }

    @Test func backspaceRemovesLastDigitDownToZero() {
        var calculator = Calculator(lifePoints: 8000)
        calculator.append("45")
        calculator.backspace()
        #expect(calculator.operandText == "4")
        calculator.backspace()
        #expect(calculator.operandText == "0")
        calculator.backspace()
        #expect(calculator.operandText == "0")
    }

    @Test(arguments: [
        (CalculatorMode.add, 9000),
        (CalculatorMode.subtract, 7000),
        (CalculatorMode.set, 1000),
    ])
    func resultDependsOnMode(mode: CalculatorMode, expected: Int) {
        var calculator = Calculator(lifePoints: 8000, mode: mode)
        calculator.append("1000")
        #expect(calculator.result == expected)
    }

    @Test func resultIsClamped() {
        var subtract = Calculator(lifePoints: 1000, mode: .subtract)
        subtract.append("5000")
        #expect(subtract.result == LifePoints.minimum)

        var add = Calculator(lifePoints: 99000, mode: .add)
        add.append("5000")
        #expect(add.result == LifePoints.maximum)
    }

    @Test func modeCyclesSetSubtractAdd() {
        var calculator = Calculator(lifePoints: 8000, mode: .set)
        calculator.cycleMode()
        #expect(calculator.mode == .subtract)
        calculator.cycleMode()
        #expect(calculator.mode == .add)
        calculator.cycleMode()
        #expect(calculator.mode == .set)
    }

    @Test func halvesRoundingDown() {
        #expect(Calculator(lifePoints: 8000).halved == 4000)
        #expect(Calculator(lifePoints: 1001).halved == 500)
    }
}
