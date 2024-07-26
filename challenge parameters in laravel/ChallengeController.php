<?php

namespace App\Http\Controllers;
use App\Models\Pupil;
use App\Models\Challenge;
use Illuminate\Http\Request;

class ChallengeController extends Controller
{
    // Display a listing of the challenges
    public function index()
    {
        $challenges = Challenge::all();
        return view('challenges.index', compact('challenges'));
    }

    // Show the form for creating a new challenge
    public function create()
    {
        return view('challenges.create');
    }

    // Store a newly created challenge in storage
    public function store(Request $request)
    {
        $request->validate([
            'challengeID' => 'required|string|max:255|unique:challenges,challengeID',
            'startDate' => 'required|date',
            'endDate' => 'required|date|after_or_equal:startDate',
            'noOfQuestions' => 'required|integer',
            'duration' => 'required|integer',
            'noOfAttempts' => 'required|integer',
        ]);

        Challenge::create($request->all());

        return redirect()->route('challenges.index')->with('success', 'Challenge created successfully.');
    }

    // Display the specified challenge
    public function show($challengeID)
    {
        $challenge = Challenge::findOrFail($challengeID);
        return view('challenges.show', compact('challenge'));
    }

    // Show the form for editing the specified challenge
    public function edit($challengeID)
    {
        $challenge = Challenge::findOrFail($challengeID);
        return view('challenges.edit', compact('challenge'));
    }

    // Update the specified challenge in storage
    public function update(Request $request, $challengeID)
    {
        $request->validate([
            'startDate' => 'required|date',
            'endDate' => 'required|date|after_or_equal:startDate',
            'noOfQuestions' => 'required|integer',
            'duration' => 'required|integer',
            'noOfAttempts' => 'required|integer',
        ]);

        $challenge = Challenge::findOrFail($challengeID);
        $challenge->update($request->all());

        return redirect()->route('challenges.index')->with('success', 'Challenge updated successfully.');
    }

    // Remove the specified challenge from storage
    public function destroy($challengeID)
    {
        $challenge = Challenge::findOrFail($challengeID);
        $challenge->delete();

        return redirect()->route('challenges.index')->with('success', 'Challenge deleted successfully.');
    }
    public function completeChallenge(Request $request, $pupilId, $challengeId)
{
    // Validate the request if necessary
    $request->validate([
        // Add validation rules if any, e.g. performance metrics or completion data
    ]);

    // Fetch the pupil and challenge records
    $pupil = Pupil::findOrFail($pupilId);
    $challenge = Challenge::findOrFail($challengeId);

    // Calculate performance score based on challenge results
    $performanceScore = $this->calculatePerformanceScore($pupil, $challenge, $request->all());

    // Update the pupil's performance score
    $pupil->performance_score = $performanceScore;
    $pupil->save();

    // Optionally, log this action or notify other systems
    // ...

    return response()->json(['message' => 'Challenge completed and score updated successfully.']);
}


private function calculatePerformanceScore(Pupil $pupil, Challenge $challenge, array $metrics)
{
    // Implement your logic to calculate the score
    // For example, use metrics from the request to influence the score calculation
    $baseScore = $challenge->points_awarded;
    $performanceMetric = $metrics['performance_metric'] ?? 1; // Example metric from request

    // Adjust base score based on performance metric or other calculations
    $performanceScore = $baseScore * $performanceMetric;

    return $performanceScore;
}

}
