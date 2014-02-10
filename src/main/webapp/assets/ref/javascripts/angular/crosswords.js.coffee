window.ModXW = angular.module('crosswords', []);

ModXW.controller 'CrosswordsCtrl', ['$scope', ($scope) ->
  $scope.reportModel = 
    foundWords: ['SUBSCRIBE', 'ANY', 'HUM', 'HYDROGEN', 'RUT', 'MOMENT', 'LEFT', 'ITEM', 'ODD', 'TAPE', 'EPIC', 'MARKET', 'PATH', 'RIVER', 'SYNDROME', 'MOCK', 'POWER', 'DIM', 'KETTLE', 'FRANK', 'BOUQUET' ]
    prizeTiers: [
      id: 1
      value: 'Ticket'
      potential: 1000,
      id: 2
      value: '$4'
      potential: 1000,
      id: 3
      value: '$5'
      potential: 1000,
      id: 4
      value: '$10'
      potential: 1000,
      id: 5
      value: '$12'
      potential: 1000,
      id: 6
      value: '$15'
      potential: 1000,
      id: 7
      value: '$20'
      potential: 1000,
      id: 8
      value: '$30'
      potential: 1000,
      id: 9
      value: '$50'
      potential: 1000,
      id: 10
      value: '$60'
      potential: 1000,
      id: 11
      value: '$100'
      potential: 1000,
      id: 12
      value: '$150'
      potential: 1000,
      id: 13
      value: '$300'
      potential: 1000,
      id: 14
      value: '$1,000'
      potential: 10,
      id: 15
      value: '$3,000'
      potential: 10,
      id: 16
      value: '$20,000'
      potential: 5,
      id: 17
      value: 'No Prize'
      potential: 60000,
      id: 18
      value: 'Invalid'
      potential: 20000,
    ]
    triple_count: 0
  
  $scope.postModel =
    board: '_________________________________________________________________________________________________________________________'
    revealed: '__________________'
    bonusWord: '_____'
    bonusPrize: -1


  rows =[ [], [], [], [], [], [], [], [], [], [], [] ]
  cols =[ [], [], [], [], [], [], [], [], [], [], [] ]
  $scope.boardModel =
    fixed: []
    aligned: rows
    rotated: cols
    horizontal: rows
    vertical: cols
    cursor: null
  
  ii=0
  kk = 0
  while ii<11 
    jj=0
    while jj<11  
      nextFixedCell = new FixedCell(kk, ii, jj)
      nextAlignedCell = new RelativeCell(ii, jj, nextFixedCell)
      nextRotatedCell = new RelativeCell(jj, ii, nextFixedCell)

      rows[ii][jj] = nextAlignedCell
      cols[jj][ii] = nextRotatedCell
      $scope.boardModel.fixed[kk] = nextFixedCell;

      nextFixedCell.setAligned(nextAlignedCell)
      nextFixedCell.setRotated(nextRotatedCell)
      
      nextRotatedCell.setMirror(nextAlignedCell)
      nextAlignedCell.setMirror(nextRotatedCell)

      if(jj > 0)
        leftCell = rows[ii][jj-1];
        upperCell = cols[jj-1][ii];

        nextAlignedCell.setLeft(leftCell)
        leftCell.setRight(nextAlignedCell)

        nextRotatedCell.setAbove(upperCell)
        upperCell.setBelow(nextRotatedCell)

      if(ii > 0)
        leftCell = cols[jj][ii-1];
        upperCell = rows[ii-1][jj];

        nextRotatedCell.setLeft(leftCell)
        leftCell.setRight(nextRotatedCell)

        nextAlignedCell.setAbove(upperCell)
        upperCell.setBelow(nextAlignedCell)
      
      jj = jj + 1
    ii = ii + 1
    kk = kk + 1

    $scope.rotateView = ->
      board = $scope.boardModel
      if board.cursor
        if ! board.cursor.v_edit_ok
          return false

        # Reset word state
        board.cursor.releaseHighlight
        board.cursor = board.cursor.mirror
        board.cursor.acquireHighlight

      temp = board.aligned
      board.aligned = board.rotated
      board.rotated = temp

      return true

  $scope.getNavigationCell = ->
    return this.cursor

  $scope.onBoardClick = (x_coord, y_coord) ->
    board = $scope.boardModel

    # Return false if no editting is supported, and rotate if necessary
    # to get to editability.
    if ! board.aligned[x_coord][y_coord].h_edit_ok
      if ! board.aligned[x_coord][y_coord].v_edit_ok
        return false
      else
        temp = board.aligned
        board.aligned = board.rotated
        board.rotated = temp
        
    # Close out the old cursor, if any.
    if board.cursor
      # Clean-up old cursor
      board.cursor.releaseHighlight

    # Initialize a new cursor
    board.cursor = board.aligned[x_coord][y_coord]
    board.cursor.acquireHighlight
    
    return true
]

class FixedCell 
  constructor: (@index, @x_abs, @y_abs) ->
  
  content: '_'
  is_triple: false
  aligned: null
  rotated: null
  
  setAligned: (alignedCell) ->
    this.aligned = alignedCell
  setRotated: (rotatedCell) ->
    this.rotated = rotatedCell

class RelativeCell
  constructor: (@x_rel, @y_rel, @fixed) ->
  
  h_edit_ok: true
  v_edit_ok: true
  toAbove: null
  toBelow: null
  toLeft: null
  toRight: null

  setAbove: (adjacentCell) ->
    this.toAbove = adjacentCell
  setBelow: (adjacentCell) ->
    this.toBelow = adjacentCell
  setLeft: (adjacentCell) ->
    this.toLeft = adjacentCell
  setRight: (adjacentCell) ->
    this.toRight = adjacentCell
  setMirror: (mirrorCell) ->
    this.mirror = mirrorCell

ModXW.directive 'nestedRepeat', [ ->
  scope: 
    sourceData: '='
    outerClass: '@'
    innerClass: '@'
  restrict: 'E'
  controller: ['$scope', ($scope) ->
    $scope.numColumns = 0
    $scope.subsets = []

    registerPartition: (newPartition) ->
      $scope.subsets[$scope.numColumns++] = newPartition
      this.partitionData()

    partitionData: ->
      if $scope.numColumns == 0 || !$scope.sourceData || $scope.sourceData.length == 0
        return

      maxRows = $scope.sourceData.length / $scope.numColumns
      shortRowCount = ($scope.numColumns * maxRows) - $scope.sourceData.length
      fullRowCount = $scope.numColumns - shortRowCount

      colIdx = 0
      setValIdx = 0

      while colIdx < fullRowCount 
        colValIdx = 0
        $scope.subsets[colIdx].splice(0)
        while colValIdx < maxRows
          $scope.subsets[colIdx][colValIdx] = $scope.sourceData[setValIdx]
          colValIdx++
          setValIdx++
        colIdx++

      maxRows = maxRows - 1
      while colIdx < $scope.numColumns
        colValIdx = 0
        $scope.subsets[colIdx].splice(0)
        while colValIdx < maxRows
          $scope.subsets[colIdx][colValIdx] = $scope.sourceData[setValIdx]
          colValIdx++
          setValIdx++
        colIdx++
      return
  ],
  link: ($scope, $element, $attr, nestedRepeat) ->
    $scope.$watchCollection 'sourceData', (collection) ->
      nestedRepeat.partitionData()
]

ModXW.directive 'partition', [ ->
  scope: true
  restrict: 'E'
  require: '^nestedRepeat'
  replace: true
  partition: 1001
  template: '<div ng-class="outerClass"><div ng-class="innerClass" ng-repeat="dataItem in columnData" ng-transclude></div></div>'
  transclude: true
  link: ($scope, $iElement, $iAttrs, nestedRepeat) ->
    $scope.columnData = []
    nestedRepeat.registerPartition($scope.columnData)
]

