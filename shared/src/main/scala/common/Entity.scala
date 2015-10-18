package common

// Subscribe UserConnection
case class SubscribeEvent(subscriber: String)

// Unsubscribe UserConnection
case class UnsubscribeEvent(unsubscriber: String)
  
// Stock: Open, Interval, Current, Diff
case class StockInfo(symbol: String,
                     current: Double,
                     // open: Double,
                     low: Double,
                     high: Double,
                     diff: Double)


//yc_teams
case class Team(id: Int,
				league: String,
				name: String,
				played: Int,
				current_points: Double,
				current_global_difference: Double, 
				expeceted_remaining_points: Double, 
				total_expected_points: Double,
				mean_expected_points: Double,
				training_set_size: Int, 
				training_error: Double)

//yc_matches
case class Match(id: Int,
				kickoff: String,
				league: String,
				home_team: String,
				model_home_expected_points: Double,
				market_home_expected_points: Double,
				home_error: Double,
				away_team: String,
				model_away_expected_points: Double,
				market_away_expected_points: Double,
				away_error: Double)

//yc_marks
case class Mark(id: Int,
				league: String,
				market: String, 
				team: String,
				bidder: String, 
				bid: Double, 
				offer: Double,
				seller: String, 
				mark: Double, 
				risk: Double,
				value_type: String, 
				value: Double, 
				risk_adjusted_value: Double,
				roe: Double)

//long term bets
case class LongTermBet(id: Int,
				date: String,
				league: String,
				market: String,
				team: String,
				CP: String,
				size: Int,
				price: Double,
				mark: Double,
				value: Int)

//hedge bets
case class HedgeBet(id: Int,
				date: String,
				league: String,
				fmatch: String,
				selection: String,
				CP: String,
				size: Int,
				price: Double,
				result: String,
				PL: Int)

//net risks
case class MatchRisk(id: Int,
				kickoff: String,
				league: String,
				match_name: String,
				val_1: Int,
				val_x: Int,
				val_2: Int)

//rating risks
case class RatingRisk(id: Int,
				league: String,
				market: String,
				team: String,
				coverage: Int,
				delta: Double,
				LS: String,
				risk: Int)

