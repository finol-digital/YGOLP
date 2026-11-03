import XCTest

@MainActor
final class YGOLPUITests: XCTestCase {
    private let app = XCUIApplication()

    override func setUp() async throws {
        continueAfterFailure = false
        app.launchArguments = ["-uiTesting"]
        app.launch()
        // Taps sent during the launch transition can be dropped; wait until the app is ready.
        XCTAssertTrue(lifePoints(1).waitForExistence(timeout: 10))
        try await Task.sleep(for: .seconds(1))
    }

    private func waitForLabel(_ label: String, of element: XCUIElement) {
        let expectation = XCTNSPredicateExpectation(predicate: NSPredicate(format: "label == %@", label), object: element)
        XCTAssertEqual(XCTWaiter().wait(for: [expectation], timeout: 5), .completed, "Expected label \(label)")
    }

    private func lifePoints(_ player: Int) -> XCUIElement {
        app.staticTexts["lifePoints-\(player)"]
    }

    private func zone(_ identifier: String) -> XCUIElement {
        app.descendants(matching: .any)[identifier].firstMatch
    }

    private func attachScreenshot(_ name: String) {
        let attachment = XCTAttachment(screenshot: XCUIScreen.main.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }

    func testStartsAtStartingLifePoints() {
        XCTAssertTrue(lifePoints(1).waitForExistence(timeout: 5))
        XCTAssertEqual(lifePoints(1).label, "Player 1: 8000 life points")
        attachScreenshot("Player 1")
    }

    func testLoseLifePointsWithCalculator() {
        zone("lose-1").tap()
        XCTAssertTrue(app.staticTexts["operand"].waitForExistence(timeout: 5))
        attachScreenshot("Calculator")

        app.buttons["1"].tap()
        app.buttons["Triple zero"].tap()
        XCTAssertEqual(app.staticTexts["operand"].label, "1000")
        app.buttons["Submit"].tap()

        waitForLabel("Player 1: 7000 life points", of: lifePoints(1))
    }

    func testOperatorCyclesAndDiscardKeepsLifePoints() {
        zone("set-1").tap()
        XCTAssertTrue(app.buttons["Set"].waitForExistence(timeout: 5))
        app.buttons["Set"].tap()
        XCTAssertTrue(app.buttons["Subtract"].exists)
        app.buttons["Subtract"].tap()
        XCTAssertTrue(app.buttons["Add"].exists)
        app.buttons["5"].tap()
        app.buttons["Discard"].tap()

        XCTAssertTrue(lifePoints(1).waitForExistence(timeout: 5))
        XCTAssertEqual(lifePoints(1).label, "Player 1: 8000 life points")
    }

    func testHalveAndSwipeToPlayerTwo() {
        app.swipeLeft()
        XCTAssertTrue(lifePoints(2).waitForExistence(timeout: 5))
        attachScreenshot("Player 2")

        zone("gain-2").tap()
        XCTAssertTrue(app.buttons["Halve"].waitForExistence(timeout: 5))
        app.buttons["Halve"].tap()

        waitForLabel("Player 2: 4000 life points", of: lifePoints(2))
    }

    func testDuelMenuRestartsDuel() {
        zone("set-1").tap()
        XCTAssertTrue(app.buttons["Submit"].waitForExistence(timeout: 5))
        app.buttons["5"].tap()
        app.buttons["Submit"].tap()
        waitForLabel("Player 1: 5 life points", of: lifePoints(1))

        app.buttons["duelMenu"].firstMatch.tap()
        XCTAssertTrue(app.buttons["Restart Duel"].waitForExistence(timeout: 5))
        attachScreenshot("Duel menu")
        app.buttons["Restart Duel"].tap()
        waitForLabel("Player 1: 8000 life points", of: lifePoints(1))
    }
}
