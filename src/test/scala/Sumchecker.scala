package net.nebupookins.akka.sendmoremoney

import akka.actor._
import akka.testkit._

import com.typesafe.config._

import org.scalatest._
import org.scalatest.matchers._

import scala.actors.Futures

import SendMoreMoney._

class SumCheckerSpec extends FlatSpec {
	val rootConf: Config = ConfigFactory.load()
	val appConf: Config = rootConf.getConfig("send-more-money")

	def shouldFindSolutionFor(word1: String, word2: String, wordTotal: String, number1: Int, number2: Int, numberTotal: Int) {
		val system = ActorSystem("SumCheckerSpec")
		new TestKit(system) with ImplicitSender {
			val sumCheckerRef = TestActorRef(new SumChecker(appConf))
			val sumChecker = sumCheckerRef.underlyingActor
			sumCheckerRef ! PotentialMatch(word1, word2, wordTotal, None, None)
			receiveN(1)(0) match {
				case PotentialMatch(w1, w2, wt, Some(addProof), _) =>
					assert(w1 === word1)
					assert(w2 === word2)
					assert(wt === wordTotal)
					assert(
						addProof.exists(proof =>
							proof.operand1 == number1 &&
							proof.operand2 == number2 &&
							proof.total == numberTotal
						)
					)
				case _ =>
					fail()
			}
			system.shutdown()
		}
	}

	it should "find a solution for SN + MO = MON (95 + 10 = 105)" in {
		shouldFindSolutionFor("SN", "MO", "MON", 95, 10, 105)
	}

	it should "find a solution for SEA + MOR = MONE (957 + 108 = 1065)" in {
		shouldFindSolutionFor("SEA", "MOR", "MONE", 957, 108, 1065)
	}

	def shouldFindAssignment(
		word1: String, word2: String, wordTotal: String,
		unassignedNumbers: Set[Int], mappingSoFar: Map[Char, Int], carry: Int = 0
	) {
		implicit val system = ActorSystem("SumCheckerSpec")
		val sumCheckerRef = TestActorRef(new SumChecker(appConf))
		val sumChecker = sumCheckerRef.underlyingActor
		val availableLetters: Set[Char] = (word1 + word2 + wordTotal).toSet
		val unassignedLetters = availableLetters -- mappingSoFar.keys
		val assignments = sumChecker.getLegalAssignments(
			word1, word2, wordTotal,
			unassignedLetters, unassignedNumbers,
			mappingSoFar, carry
		)
		assert(assignments.nonEmpty, "Assignments was empty.")
		system.shutdown()
	}

	it should "find a legal assignment for SEA + MOR = MONE (957 + 108 = 1065)" in {
		shouldFindAssignment("S", "M", "MO", Set(1,2,3,4,6,9), Map('A' -> 7, 'R' -> 8, 'N' -> 5, 'O' -> 0), carry = 0)
		shouldFindAssignment("SN", "MO", "MON", Set(1,2,3,4,6,9), Map('A' -> 7, 'R' -> 8, 'N' -> 5, 'O' -> 0), carry = 0)
		shouldFindAssignment("SEA", "MOR", "MONE", Set(), Map('A' -> 7, 'R' -> 8, 'E' -> 5, 'O' -> 0, 'N' -> 6, 'S' -> 9, 'M' -> 1))
		shouldFindAssignment("SEA", "MOR", "MONE", Set(1,2,3,4), Map('A' -> 7, 'R' -> 8, 'E' -> 5, 'O' -> 0, 'N' -> 6, 'S' -> 9))
		shouldFindAssignment("SEA", "MOR", "MONE", Set(1,2,3,4,9), Map('A' -> 7, 'R' -> 8, 'E' -> 5, 'O' -> 0, 'N' -> 6))
		shouldFindAssignment("SEA", "MOR", "MONE", Set(1,2,3,4,6,9), Map('A' -> 7, 'R' -> 8, 'E' -> 5, 'O' -> 0))
		shouldFindAssignment("SE", "MO", "MON", Set(0,1,9), Map('A' -> 7, 'R' -> 8, 'E' -> 5, 'N' -> 6, 'O' -> 0), carry = 1)
		shouldFindAssignment("SE", "MO", "MON", Set(0,1,9), Map('A' -> 7, 'R' -> 8, 'E' -> 5, 'N' -> 6), carry = 1)
		shouldFindAssignment("SE", "MO", "MON", Set(0,1,6,9), Map('A' -> 7, 'R' -> 8, 'E' -> 5), carry = 1)
		shouldFindAssignment("SEA", "MOR", "MONE", Set(0,1,6,9), Map('A' -> 7, 'R' -> 8, 'E' -> 5))
		shouldFindAssignment("SEA", "MOR", "MONE", Set(0,1,2,3,4,6,9), Map('A' -> 7, 'R' -> 8, 'E' -> 5))
		shouldFindAssignment("SEA", "MOR", "MONE", Set(0,1,2,3,4,6,9), Map('A' -> 7, 'R' -> 8, 'E' -> 5))
		shouldFindAssignment("SEA", "MOR", "MONE", Set(0,1,2,3,4,5,6,9), Map('A' -> 7, 'R' -> 8))
		shouldFindAssignment("SEA", "MOR", "MONE", Set(0,1,2,3,4,5,6,8,9), Map('A' -> 7))
		shouldFindAssignment("SEA", "MOR", "MONE", (0 until 10).toSet, Map.empty)
	}

	it should "find a solution for SEND + MORE = MONEY (9567 + 1085 = 10652)" in {
		shouldFindSolutionFor("SEND", "MORE", "MONEY", 9567, 1085, 10652)
	}
}